/*
 * 07/29/2012
 *
 * This library is distributed under a modified BSD license.  See the included
 * ZScriptLanguageSupport.License.txt file for details.
 */
package org.fife.rsta.zscript.ast;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.Position;

import org.fife.rsta.zscript.IconFactory;
import org.fife.rsta.zscript.IconFactory.IconData;


/**
 * A script in a ZScript file.  Scripts contain collections of functions and
 * variables, though the latter are deprecated.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ScriptNode extends MemberNode implements MemberContainer, BodiedNode {

	private Position bodyStart;
	private Position bodyEnd;
	private List<FunctionDecNode> functions;
	
	/** Note that script-scoped vars are deprecated in 2.5. */
	private List<VariableDecNode> vars;


	public ScriptNode(Position start) {
		super(SCRIPT_DEC, start);
		functions = new ArrayList<FunctionDecNode>();
		vars = new ArrayList<VariableDecNode>();
	}


	public void accept(ZScriptAstVisitor visitor) {

		boolean visitChildren = visitor.visit(this);

		if (visitChildren) {
			for (int i=0; i<getFunctionCount(); i++) {
				getFunction(i).accept(visitor);
			}

		}

		visitor.postVisit(this);

	}


	public void addFunctionDec(FunctionDecNode function) {
		functions.add(function);
	}


	public ShadowedVarInfo addVariableDec(VariableDecNode variable) {
		VariableDecNode prev = getVariableDecByName(variable.getName());
		vars.add(variable);
		return prev==null ? null : new ShadowedVarInfo(prev);
	}


	public boolean bodyContainsOffset(int offs) {
		return offs>=getBodyStartOffset() && offs<getBodyEndOffset();
	}


	public int getBodyEndOffset() {
		return bodyEnd==null ? Integer.MAX_VALUE : bodyEnd.getOffset();
	}


	public int getBodyStartOffset() {
		return bodyStart.getOffset();
	}


	public BodiedNode getDeepestBodiedNodeContaining(int offs) {
		if (bodyContainsOffset(offs)) { // Should always be true
			for (int i=0; i<getFunctionCount(); i++) {
				FunctionDecNode func = getFunction(i);
				if (func.bodyContainsOffset(offs)) {
					return func.getDeepestBodiedNodeContaining(offs);
				}
			}
			return this;
		}
		return null;
	}


	public FunctionDecNode getFunction(int index) {
		return functions.get(index);
	}


	public int getFunctionCount() {
		return functions.size();
	}


	@Override
	public IconData getIcon() {
		return new IconData(IconFactory.SCRIPT_ICON, false);
	}


	public int getVariableCount() {
		return vars.size();
	}


	public VariableDecNode getVariableDec(int index) {
		return vars.get(index);
	}


	public VariableDecNode getVariableDecByName(String name) {
		for (int i=0; i<getVariableCount(); i++) {
			VariableDecNode var = getVariableDec(i);
			if (name.equals(var.getName())) {
				return var;
			}
		}
		return null;
	}


	public void setBodyEnd(Position end) {
		bodyEnd = end;
	}


	public void setBodyStart(Position start) {
		bodyStart = start;
	}


	@Override
	public String toString(boolean colored) {

		StringBuffer sb = new StringBuffer();
		if (colored) {
			sb.append("<html>");
		}

		sb.append(getName()).append(" : ");
		if (colored) {
			sb.append("<font color='#808080'>");
		}
		sb.append(getType());

		return sb.toString();

	}


}