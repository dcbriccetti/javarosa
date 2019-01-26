/*
 * Copyright (C) 2009 JavaRosa
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.javarosa.xpath.expr;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.core.model.instance.DataInstance;
import org.javarosa.core.util.externalizable.DeserializationException;
import org.javarosa.core.util.externalizable.ExtUtil;
import org.javarosa.core.util.externalizable.PrototypeFactory;

import static java.lang.Math.abs;
import static org.javarosa.xpath.expr.XPathFuncExpr.toBoolean;
import static org.javarosa.xpath.expr.XPathFuncExpr.toNumeric;
import static org.javarosa.xpath.expr.XPathFuncExpr.unpack;

public class XPathEqExpr extends XPathBinaryOpExpr {
    public boolean equal;

    /** Deserialization constructor */
    public XPathEqExpr() {
    }

    public XPathEqExpr(boolean equal, XPathExpression a, XPathExpression b) {
        super(a, b);
        this.equal = equal;
    }

    @Override
    public Object eval(DataInstance model, EvaluationContext evalContext) {
        final Object aval = unpack(a.eval(model, evalContext));
        final Object bval = unpack(b.eval(model, evalContext));
        final boolean eq;

        if (aval instanceof Boolean || bval instanceof Boolean) {
            boolean a = aval instanceof Boolean ? (Boolean) aval : toBoolean(aval);
            boolean b = bval instanceof Boolean ? (Boolean) bval : toBoolean(bval);
            eq = a == b;
        } else if (aval instanceof Double || bval instanceof Double) {
            double a = aval instanceof Double ? (Double) aval : toNumeric(aval);
            double b = bval instanceof Double ? (Double) bval : toNumeric(bval);
            eq = abs(a - b) < 1e-12;
        } else {
            eq = XPathFuncExpr.toString(aval).equals(XPathFuncExpr.toString(bval));
        }

        return equal == eq;
    }

    public String toString() {
        return super.toString(equal ? "==" : "!=");
    }

    public boolean equals(Object o) {
        return o instanceof XPathEqExpr && (super.equals(o) && equal == ((XPathEqExpr) o).equal);
    }

    @Override
    public void readExternal(DataInputStream in, PrototypeFactory pf) throws IOException, DeserializationException {
        equal = ExtUtil.readBool(in);
        super.readExternal(in, pf);
    }

    @Override
    public void writeExternal(DataOutputStream out) throws IOException {
        ExtUtil.writeBool(out, equal);
        super.writeExternal(out);
    }
}
