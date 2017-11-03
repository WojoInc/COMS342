package funclang;

import java.util.ArrayList;
import java.util.List;


/**
 * This class hierarchy represents expressions in the abstract syntax tree
 * manipulated by this interpreter.
 * 
 * @author hridesh
 * 
 */
@SuppressWarnings("rawtypes")
public interface AST {
	public static abstract class ASTNode implements AST {
		public abstract Object accept(Visitor visitor, Env env);
	}

	public static class Program extends ASTNode {
		List<DefineDecl> _decls;
		Exp _e;

		public Program(List<DefineDecl> decls, Exp e) {
			_decls = decls;
			_e = e;
		}

		public Exp e() {
			return _e;
		}

		public List<DefineDecl> decls() {
			return _decls;
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	public static abstract class Exp extends ASTNode {

	}

	public static class VarExp extends Exp {
		String _name;

		public VarExp(String name) {
			_name = name;
		}

		public String name() {
			return _name;
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	public static class UnitExp extends Exp {

		public UnitExp() {
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}

	}

	public static class NumExp extends Exp {
		double _val;

		public NumExp(double v) {
			_val = v;
		}

		public double v() {
			return _val;
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	public static class StrExp extends Exp {
		String _val;

		public StrExp(String v) {
			_val = v;
		}

		public String v() {
			return _val;
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	public static class BoolExp extends Exp {
		boolean _val;

		public BoolExp(boolean v) {
			_val = v;
		}

		public boolean v() {
			return _val;
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	public static abstract class CompoundArithExp extends Exp {
		List<Exp> _rest;

		public CompoundArithExp() {
			_rest = new ArrayList<Exp>();
		}

		public CompoundArithExp(Exp fst) {
			_rest = new ArrayList<Exp>();
			_rest.add(fst);
		}

		public CompoundArithExp(List<Exp> args) {
			_rest = new ArrayList<Exp>();
			for (Exp e : args)
				_rest.add((Exp) e);
		}

		public CompoundArithExp(Exp fst, List<Exp> rest) {
			_rest = new ArrayList<Exp>();
			_rest.add(fst);
			_rest.addAll(rest);
		}

		public CompoundArithExp(Exp fst, Exp second) {
			_rest = new ArrayList<Exp>();
			_rest.add(fst);
			_rest.add(second);
		}

		public Exp fst() {
			return _rest.get(0);
		}

		public Exp snd() {
			return _rest.get(1);
		}

		public List<Exp> all() {
			return _rest;
		}

		public void add(Exp e) {
			_rest.add(e);
		}

	}

	public static class AddExp extends CompoundArithExp {
		public AddExp(Exp fst) {
			super(fst);
		}

		public AddExp(List<Exp> args) {
			super(args);
		}

		public AddExp(Exp fst, List<Exp> rest) {
			super(fst, rest);
		}

		public AddExp(Exp left, Exp right) {
			super(left, right);
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	public static class SubExp extends CompoundArithExp {

		public SubExp(Exp fst) {
			super(fst);
		}

		public SubExp(List<Exp> args) {
			super(args);
		}

		public SubExp(Exp fst, List<Exp> rest) {
			super(fst, rest);
		}

		public SubExp(Exp left, Exp right) {
			super(left, right);
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	public static class DivExp extends CompoundArithExp {
		public DivExp(Exp fst) {
			super(fst);
		}

		public DivExp(List<Exp> args) {
			super(args);
		}

		public DivExp(Exp fst, List<Exp> rest) {
			super(fst, rest);
		}

		public DivExp(Exp left, Exp right) {
			super(left, right);
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	public static class MultExp extends CompoundArithExp {
		public MultExp(Exp fst) {
			super(fst);
		}

		public MultExp(List<Exp> args) {
			super(args);
		}

		public MultExp(Exp fst, List<Exp> rest) {
			super(fst, rest);
		}

		public MultExp(Exp left, Exp right) {
			super(left, right);
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	/**
	 * A let expression has the syntax
	 * <p>
	 * (let ((name expression)* ) expression)
	 *
	 * @author hridesh
	 */
	public static class LetExp extends Exp {
		List<String> _names;
		List<Exp> _value_exps;
		Exp _body;

		public LetExp(List<String> names, List<Exp> value_exps, Exp body) {
			_names = names;
			_value_exps = value_exps;
			_body = body;
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}

		public List<String> names() {
			return _names;
		}

		public List<Exp> value_exps() {
			return _value_exps;
		}

		public Exp body() {
			return _body;
		}

	}

	/**
	 * A define declaration has the syntax
	 * <p>
	 * (define name expression)
	 *
	 * @author hridesh
	 */
	public static class DefineDecl extends Exp {
		String _name;
		Exp _value_exp;

		public DefineDecl(String name, Exp value_exp) {
			_name = name;
			_value_exp = value_exp;
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}

		public String name() {
			return _name;
		}

		public Exp value_exp() {
			return _value_exp;
		}

	}

	/**
	 * An anonymous procedure declaration has the syntax
	 *
	 * @author hridesh
	 */
	public static class LambdaExp extends Exp {
		List<String> _formals;
		Exp _body;
		NumExp _defParam;

		public LambdaExp(List<String> formals, Exp body, NumExp defParam) {
			_formals = formals;
			_body = body;
			_defParam = defParam;
		}

		public List<String> formals() {
			return _formals;
		}

		public Exp body() {
			return _body;
		}

		public NumExp defParam() {
			return _defParam;
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	/**
	 * A call expression has the syntax
	 *
	 * @author hridesh
	 */
	public static class CallExp extends Exp {
		Exp _operator;
		List<Exp> _operands;

		public CallExp(Exp operator, List<Exp> operands) {
			_operator = operator;
			_operands = operands;
		}

		public Exp operator() {
			return _operator;
		}

		public List<Exp> operands() {
			return _operands;
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	/**
	 * An if expression has the syntax
	 * <p>
	 * (if conditional_expression true_expression false_expression)
	 *
	 * @author hridesh
	 */
	public static class IfExp extends Exp {
		Exp _conditional;
		Exp _then_exp;
		Exp _else_exp;

		public IfExp(Exp conditional, Exp then_exp, Exp else_exp) {
			_conditional = conditional;
			_then_exp = then_exp;
			_else_exp = else_exp;
		}

		public Exp conditional() {
			return _conditional;
		}

		public Exp then_exp() {
			return _then_exp;
		}

		public Exp else_exp() {
			return _else_exp;
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	/**
	 * A less expression has the syntax
	 * <p>
	 * ( < first_expression second_expression )
	 *
	 * @author hridesh
	 */
	public static class LessExp extends BinaryComparator {
		public LessExp(Exp first_exp, Exp second_exp) {
			super(first_exp, second_exp);
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	public static abstract class BinaryComparator extends Exp {
		private Exp _first_exp;
		private Exp _second_exp;

		BinaryComparator(Exp first_exp, Exp second_exp) {
			_first_exp = first_exp;
			_second_exp = second_exp;
		}

		public Exp first_exp() {
			return _first_exp;
		}

		public Exp second_exp() {
			return _second_exp;
		}
	}

	/**
	 * An equal expression has the syntax
	 * <p>
	 * ( == first_expression second_expression )
	 *
	 * @author hridesh
	 */
	public static class EqualExp extends BinaryComparator {
		public EqualExp(Exp first_exp, Exp second_exp) {
			super(first_exp, second_exp);
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	/**
	 * A greater expression has the syntax
	 * <p>
	 * ( > first_expression second_expression )
	 *
	 * @author hridesh
	 */
	public static class GreaterExp extends BinaryComparator {
		public GreaterExp(Exp first_exp, Exp second_exp) {
			super(first_exp, second_exp);
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	/**
	 * A car expression has the syntax
	 * <p>
	 * ( car expression )
	 *
	 * @author hridesh
	 */
	public static class CarExp extends Exp {
		private Exp _arg;

		public CarExp(Exp arg) {
			_arg = arg;
		}

		public Exp arg() {
			return _arg;
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	/**
	 * A cdr expression has the syntax
	 * <p>
	 * ( car expression )
	 *
	 * @author hridesh
	 */
	public static class CdrExp extends Exp {
		private Exp _arg;

		public CdrExp(Exp arg) {
			_arg = arg;
		}

		public Exp arg() {
			return _arg;
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	/**
	 * A cons expression has the syntax
	 * <p>
	 * ( cons expression expression )
	 *
	 * @author hridesh
	 */
	public static class ConsExp extends Exp {
		private Exp _fst;
		private Exp _snd;

		public ConsExp(Exp fst, Exp snd) {
			_fst = fst;
			_snd = snd;
		}

		public Exp fst() {
			return _fst;
		}

		public Exp snd() {
			return _snd;
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	/**
	 * A list expression has the syntax
	 * <p>
	 * ( list expression* )
	 *
	 * @author hridesh
	 */
	public static class ListExp extends Exp {
		private List<Exp> _elems;

		public ListExp(List<Exp> elems) {
			_elems = elems;
		}

		public List<Exp> elems() {
			return _elems;
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	/**
	 * A null expression has the syntax
	 * <p>
	 * ( null? expression )
	 *
	 * @author hridesh
	 */
	public static class NullExp extends Exp {
		private Exp _arg;

		public NullExp(Exp arg) {
			_arg = arg;
		}

		public Exp arg() {
			return _arg;
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	/**
	 * Eval expression: evaluate the program that is val_exp
	 *
	 * @author hridesh
	 */
	public static class EvalExp extends Exp {
		private Exp _code;

		public EvalExp(Exp code) {
			_code = code;
		}

		public Exp code() {
			return _code;
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	/**
	 * Read expression: reads the file that is _file
	 *
	 * @author hridesh
	 */
	public static class ReadExp extends Exp {
		private Exp _file;

		public ReadExp(Exp file) {
			_file = file;
		}

		public Exp file() {
			return _file;
		}

		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	public abstract class PredExp extends Exp {
		private Exp _exp;

		public Exp exp() {
			return _exp;
		}

		public PredExp(Exp exp) {
			_exp = exp;
		}
	}

	public static class NumPredExp extends PredExp {
		public NumPredExp(Exp exp) {
			super(exp);
		}

		@Override
		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	public static class BoolPredExp extends PredExp {

		public BoolPredExp(Exp exp) {
			super(exp);
		}

		@Override
		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	public static class StrPredExp extends PredExp {

		public StrPredExp(Exp exp) {
			super(exp);
		}

		@Override
		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	public static class ProcPredExp extends PredExp {

		public ProcPredExp(Exp exp) {
			super(exp);
		}

		@Override
		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	public static class PairPredExp extends PredExp {

		public PairPredExp(Exp exp) {
			super(exp);
		}

		@Override
		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	public static class ListPredExp extends PredExp {

		public ListPredExp(Exp exp) {
			super(exp);
		}

		@Override
		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	public static class UnitPredExp extends PredExp {

		public UnitPredExp(Exp exp) {
			super(exp);
		}

		@Override
		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	public static class RefExp extends Exp {
		private Exp val_exp;

		public RefExp(Exp val) {
			val_exp = val;
		}

		public Exp val_exp() {
			return val_exp;
		}

		@Override
		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	public static class DerefExp extends Exp {
		private Exp loc_exp;

		public DerefExp(Exp r_val) {
			loc_exp = r_val;
		}

		public Exp loc_exp() {
			return loc_exp;
		}

		@Override
		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	public static class SetrefExp extends Exp {
		private Exp val_exp;
		private Exp loc_exp;

		public SetrefExp(Exp rval, Exp val) {
			val_exp = val;
			loc_exp = rval;
		}

		public Exp val_exp() {
			return val_exp;
		}

		public Exp loc_exp() {
			return loc_exp;
		}

		@Override
		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	public static class FreeExp extends Exp {
		private Exp loc_exp;

		public FreeExp(Exp r_val) {
			loc_exp = r_val;
		}

		public Exp loc_exp() {
			return loc_exp;
		}

		@Override
		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	public static class ArrayExp extends Exp {
		private List<Exp> _dims;

		public ArrayExp(List<Exp> _dims) {
			this._dims = _dims;
		}

		public List<Exp> dims() {
			return _dims;
		}

		@Override
		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	public static class IndexExp extends Exp {
		private Exp _arr;
		private List<Exp> _idxs;

		public IndexExp(Exp _arr, List<Exp> _idxs) {
			this._arr = _arr;
			this._idxs = _idxs;
		}

		public Exp arr() {
			return _arr;
		}

		public List<Exp> idxs() {
			return _idxs;
		}

		@Override
		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	public static class ArrAssignExp extends Exp {
		private Exp _arr;
		private List<Exp> _idxs;
		private Exp _val;

		public ArrAssignExp(Exp _arr, List<Exp> _idxs, Exp _val) {
			this._arr = _arr;
			this._idxs = _idxs;
			this._val = _val;
		}

		public Exp arr() {
			return _arr;
		}

		public List<Exp> idxs() {
			return _idxs;
		}

		public Exp val() {
			return _val;
		}

		@Override
		public Object accept(Visitor visitor, Env env) {
			return visitor.visit(this, env);
		}
	}

	public interface Visitor <T> {
		// This interface should contain a signature for each concrete AST node.
		public T visit(AST.AddExp e, Env env);
		public T visit(AST.UnitExp e, Env env);
		public T visit(AST.NumExp e, Env env);
		public T visit(AST.StrExp e, Env env);
		public T visit(AST.BoolExp e, Env env);
		public T visit(AST.DivExp e, Env env);
		public T visit(AST.MultExp e, Env env);
		public T visit(AST.Program p, Env env);
		public T visit(AST.SubExp e, Env env);
		public T visit(AST.VarExp e, Env env);
		public T visit(AST.LetExp e, Env env); // New for the varlang
		public T visit(AST.DefineDecl d, Env env); // New for the definelang
		public T visit(AST.ReadExp e, Env env); // New for the funclang
		public T visit(AST.EvalExp e, Env env); // New for the funclang
		public T visit(AST.LambdaExp e, Env env); // New for the funclang
		public T visit(AST.CallExp e, Env env); // New for the funclang
		public T visit(AST.IfExp e, Env env); // Additional expressions for convenience
		public T visit(AST.LessExp e, Env env); // Additional expressions for convenience
		public T visit(AST.EqualExp e, Env env); // Additional expressions for convenience
		public T visit(AST.GreaterExp e, Env env); // Additional expressions for convenience
		public T visit(AST.CarExp e, Env env); // Additional expressions for convenience
		public T visit(AST.CdrExp e, Env env); // Additional expressions for convenience
		public T visit(AST.ConsExp e, Env env); // Additional expressions for convenience
		public T visit(AST.ListExp e, Env env); // Additional expressions for convenience
		public T visit(AST.NullExp e, Env env); // Additional expressions for convenience
		public T visit(AST.NumPredExp e, Env env);
		public T visit(AST.BoolPredExp e, Env env);
		public T visit(AST.StrPredExp e, Env env);
		public T visit(AST.ProcPredExp e, Env env);
		public T visit(AST.PairPredExp e, Env env);
		public T visit(AST.ListPredExp e, Env env);
		public T visit(AST.UnitPredExp e, Env env);
		public T visit(AST.RefExp e, Env env);
		public T visit(AST.DerefExp e, Env env);
		public T visit(AST.SetrefExp e, Env env);
		public T visit(AST.FreeExp e, Env env);
		public T visit(AST.ArrayExp e, Env env);
		public T visit(AST.ArrAssignExp e, Env env);
		public T visit(AST.IndexExp e, Env env);
	}
}
