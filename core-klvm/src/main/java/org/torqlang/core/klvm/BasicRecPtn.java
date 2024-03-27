/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.SourceSpan;

import java.util.ArrayList;
import java.util.List;

import static org.torqlang.core.util.ListTools.nullSafeCopyOf;

@SuppressWarnings("ClassCanBeRecord")
public final class BasicRecPtn implements RecPtn {

    private final LiteralOrIdentPtn label;
    private final List<FieldPtn> fields;
    private final boolean partialArity;
    private final SourceSpan sourceSpan;

    public BasicRecPtn(LiteralOrIdentPtn label, List<FieldPtn> fields, boolean partialArity, SourceSpan sourceSpan) {
        this.label = label;
        this.fields = nullSafeCopyOf(fields);
        this.partialArity = partialArity;
        this.sourceSpan = sourceSpan;
    }

    private static ValueOrResolvedPtn caseRecOfResolvedRecPtn(Rec rec, ResolvedRecPtn resRecPtn, Env env)
        throws WaitException
    {
        // Return whether the label of Rec is <lit> and its arity is {<feat>1, ..., <feat>n} per [CTM p. 67].
        if (resRecPtn.partialArity) {
            if (rec.fieldCount() < resRecPtn.fieldCount()) {
                return null;
            }
        } else {
            if (rec.fieldCount() != resRecPtn.fieldCount()) {
                return null;
            }
        }
        Value recLabel = rec.label();
        Value ptnLabel = resRecPtn.label;
        if (!recLabel.entailsValueOrIdent(ptnLabel, env)) {
            return null;
        }
        // Iterate over the record pattern instead of the record value, as the record pattern may have
        // fewer features due to partial arity.
        int j = 0;
        for (int i = 0; i < resRecPtn.fieldCount(); i++) {
            ResolvedFieldPtn ptnField = resRecPtn.fields.get(i);
            Value ptnFeature = ptnField.feature;
            ValueOrVar recValue;
            // Due to partial arity, we may have to advance over unreferenced record fields
            while (true) {
                if (j >= rec.fieldCount()) {
                    return null;
                }
                Value recFeature = rec.featureAt(j);
                recValue = rec.valueAt(j);
                if (recFeature.entailsValueOrIdent(ptnFeature, env)) {
                    break;
                }
                if (!resRecPtn.partialArity) {
                    return null;
                }
                j++; // advance j so we can compare on next feature
            }
            //
            // Pattern matching is not recursive, we simply need to match combinations of Value and Ident. Also,
            // we are just matching structures, so any mention of binding is a reference to the subsequent step
            // that will deconstruct the value according to the matching pattern structure.
            //
            // Given two field values from record and pattern, respectively, we proceed as follows:
            //     Ident vs Ident -- we match and the two Idents will get bound into an equivalence set
            //     Ident vs Value -- we will generate a WaitException, below, when we attempt an entails
            //     Value vs Ident -- we match and will bind the record value to the Ident in the pattern
            //     Value vs Value -- we match if the two Values match, below, when we perform an entails
            //
            // To implement the cases above, we simply need to perform an entails when the pattern argument is
            // a type of `Value`.
            //
            if (ptnField.value instanceof Value value) {
                if (!recValue.entails(value, null)) {
                    return null;
                }
            }
            j++; // keep j in sync with i
        }
        return resRecPtn;
    }

    /*
     * This method is a polymorphic callback requesting "case Rec of RecPtn then..."
     */
    @Override
    public final ValueOrResolvedPtn caseRecOfThis(Rec rec, Env env) throws WaitException {
        rec.checkDetermined();
        Value labelRes = label.resolveValue(env);
        List<ResolvedFieldPtn> fieldsRes = new ArrayList<>();
        for (FieldPtn fp : fields) {
            Feature featureRes = (Feature) fp.feature.resolveValue(env);
            ValueOrIdent valueRes = fp.value.resolveValueOrIdent(env);
            fieldsRes.add(new ResolvedFieldPtn(featureRes, valueRes));
        }
        fieldsRes.sort(FeatureProviderComparator.SINGLETON);
        ResolvedRecPtn resolvedRecPtn = new ResolvedRecPtn(labelRes, fieldsRes, partialArity);
        return caseRecOfResolvedRecPtn(rec, resolvedRecPtn, env);
    }

    @Override
    public final List<FieldPtn> fields() {
        return fields;
    }

    @Override
    public final LiteralOrIdentPtn label() {
        return label;
    }

    @Override
    public final boolean partialArity() {
        return partialArity;
    }

    @Override
    public final SourceSpan sourceSpan() {
        return sourceSpan;
    }

    @Override
    public final String toString() {
        return toKernelString();
    }

}
