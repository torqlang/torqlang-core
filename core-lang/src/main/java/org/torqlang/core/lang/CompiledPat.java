/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.torqlang.core.klvm.*;

import java.util.ArrayList;
import java.util.List;

final class CompiledPat {

    private final Pat source;
    private final Generator generator;
    private final List<CompiledPat.ChildPtn> children;
    private ValueOrPtn root;

    CompiledPat(Pat source, Generator generator) {
        this.source = source;
        this.generator = generator;
        this.children = new ArrayList<>();
    }

    final List<CompiledPat.ChildPtn> children() {
        return children;
    }

    /*
     * pat: rec_pat | tuple_pat |
     *      (label_pat ('#' (rec_pat | tuple_pat))?) |
     *      INT_LITERAL | (ident var_type_anno?);
     *
     * label_pat: ('~' ident) | bool | STR_LITERAL | 'eof' | 'nothing';
     *
     * rec_pat: '{' (field_pat (',' field_pat)* (',' '...')?)? '}';
     *
     * tuple_pat: '[' (pat (',' pat)* (',' '...')?)? ']';
     *
     * field_pat: (feat_pat ':')? pat;
     *
     * feat_pat: ('~' ident) | bool | INT_LITERAL | STR_LITERAL |
     *           'eof' | 'nothing';
     */
    final void compile() {
        if (source instanceof IdentAsPat identAsPat) {
            Ident lhi = generator.toIdentOrNextAnonymousIdent(identAsPat.ident);
            root = new IdentPtn(lhi, identAsPat.escaped);
        } else if (source instanceof FeatureAsPat featureAsPat) {
            root = featureAsPat.value();
        } else if (source instanceof RecPat recPat) {
            root = compileRecPat(recPat);
        } else if (source instanceof TuplePat tuplePat) {
            root = compileTuplePat(tuplePat);
        } else {
            // This condition should never execute
            throw new IllegalArgumentException("Unrecognized pattern type");
        }
    }

    private FeatureOrIdentPtn compileFeaturePat(FeaturePat featurePat) {
        if (featurePat instanceof FeatureAsPat featureAsPat) {
            return featureAsPat.value();
        } else {
            IdentAsPat identAsPat = (IdentAsPat) featurePat;
            if (!identAsPat.escaped) {
                throw new IllegalStateException("A pattern feature must be a literal or an escaped identifier");
            }
            return new IdentPtn(identAsPat.ident, true);
        }
    }

    private LiteralOrIdentPtn compileLabelPat(LabelPat labelPat) {
        if (labelPat instanceof LiteralAsPat literalAsPat) {
            return literalAsPat.value();
        } else {
            IdentAsPat identAsPat = (IdentAsPat) labelPat;
            if (!identAsPat.escaped) {
                throw new IllegalStateException("A pattern label must be a literal or an escaped identifier");
            }
            return new IdentPtn(identAsPat.ident, true);
        }
    }

    private RecPtn compileRecPat(RecPat recPat) {
        LiteralOrIdentPtn labelPtn;
        if (recPat.label() != null) {
            labelPtn = compileLabelPat(recPat.label());
        } else {
            labelPtn = Rec.DEFAULT_LABEL;
        }
        List<FieldPtn> fieldPtns = new ArrayList<>(recPat.fields().size());
        for (FieldPat fp : recPat.fields()) {
            FeatureOrIdentPtn feature = compileFeaturePat(fp.feature);
            ValueOrIdentPtn value = compileValuePat(fp.value);
            fieldPtns.add(new FieldPtn(feature, value, recPat));
        }
        return new BasicRecPtn(labelPtn, fieldPtns, recPat.partialArity(), recPat);
    }

    private RecPtn compileTuplePat(TuplePat tuplePat) {
        LiteralOrIdentPtn labelPtn;
        if (tuplePat.label() != null) {
            labelPtn = compileLabelPat(tuplePat.label());
        } else {
            labelPtn = Rec.DEFAULT_LABEL;
        }
        List<FieldPtn> fieldPtns = new ArrayList<>(tuplePat.values().size());
        for (int i = 0; i < tuplePat.values().size(); i++) {
            Pat valuePat = tuplePat.values().get(i);
            Feature feature = Int32.of(i);
            ValueOrIdentPtn value = compileValuePat(valuePat);
            fieldPtns.add(new FieldPtn(feature, value, valuePat));
        }
        return new BasicRecPtn(labelPtn, fieldPtns, tuplePat.partialArity(), tuplePat);
    }

    private ValueOrIdentPtn compileValuePat(Pat pat) {
        if (pat instanceof IdentAsPat identAsPat) {
            Ident ident = generator.toIdentOrNextAnonymousIdent(identAsPat.ident);
            return new IdentPtn(ident, identAsPat.escaped);
        } else if (pat instanceof FeatureAsPat featureAsPat) {
            return featureAsPat.value();
        } else if (pat instanceof RecPat recPat) {
            Ident valueIdent = generator.allocateNextSystemVarIdent();
            IdentPtn valueIdentPtn = new IdentPtn(valueIdent);
            // Adding the child before visiting its fields ensures a pre-order construction of nested patterns.
            // Also, compiled patterns will never create an escaped child identifier, therefore ChildPtn contains
            // an Ident and not IdentPtn. The Ident is subsequently used as an operand in nested case statements.
            CompiledPat.ChildPtn childPtn = new CompiledPat.ChildPtn(valueIdent);
            children.add(childPtn);
            childPtn.setRecPtn(compileRecPat(recPat));
            return valueIdentPtn;
        } else if (pat instanceof TuplePat tuplePat) {
            // NOTE: TuplePat is a specialized RecPat. See RecPat.
            Ident valueIdent = generator.allocateNextSystemVarIdent();
            IdentPtn valueIdentPtn = new IdentPtn(valueIdent);
            CompiledPat.ChildPtn childPtn = new CompiledPat.ChildPtn(valueIdent);
            children.add(childPtn);
            childPtn.setRecPtn(compileTuplePat(tuplePat));
            return valueIdentPtn;
        } else {
            // This condition should never execute
            throw new IllegalArgumentException("Unrecognized pattern type");
        }
    }

    final ValueOrPtn root() {
        return root;
    }

    final Pat source() {
        return source;
    }

    static class ChildPtn {
        final Ident arg;
        RecPtn recPtn;

        ChildPtn(Ident arg) {
            this.arg = arg;
        }

        private void setRecPtn(RecPtn recPtn) {
            this.recPtn = recPtn;
        }
    }

}
