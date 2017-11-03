package funclang;
import static funclang.AST.*;
import static funclang.Value.*;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

import funclang.Env.*;
import javafx.util.Pair;

public class Evaluator implements Visitor<Value> {
	
	Printer.Formatter ts = new Printer.Formatter();

	Env initEnv = initialEnv(); //New for definelang
	Heap heap = new Heap16Bit();
	
	Value valueOf(Program p) {
		return (Value) p.accept(this, initEnv);
	}

	public static boolean compareValues(Value val1, Value val2){
		if(val1 instanceof NumVal && val2 instanceof NumVal){
			return ((NumVal) val1).v() == ((NumVal) val2).v();
		}
		else if(val1 instanceof StringVal && val2 instanceof StringVal){
			String s1 = ((StringVal) val1).v();
			String s2 = ((StringVal) val2).v();
			return s1.equals(s2);
		}
		else if(val1 instanceof FunVal && val2 instanceof FunVal){
			return val1 == val2;
		}
		else if(val1 instanceof BoolVal && val2 instanceof BoolVal){
			return ((BoolVal) val1).v() == ((BoolVal) val2).v();
		}
		else if(val1 instanceof PairVal && val2 instanceof PairVal){
			boolean b1 = compareValues(((PairVal) val1).fst(), ((PairVal) val2).fst());
			boolean b2 = compareValues(((PairVal) val1).snd(), ((PairVal) val2).snd());

			return b1 && b2;
		}
		else if(val1 instanceof Null && val2 instanceof Null){
			return true;
		}
		else return false;

	}
	
	@Override
	public Value visit(AddExp e, Env env) {
		List<Exp> operands = e.all();
		double result = 0;
		for(Exp exp: operands) {
			NumVal intermediate = (NumVal) exp.accept(this, env); // Dynamic type-checking
			result += intermediate.v(); //Semantics of AddExp in terms of the target language.
		}
		return new NumVal(result);
	}
	
	@Override
	public Value visit(UnitExp e, Env env) {
		return new UnitVal();
	}

	@Override
	public Value visit(NumExp e, Env env) {
		return new NumVal(e.v());
	}

	@Override
	public Value visit(StrExp e, Env env) {
		return new StringVal(e.v());
	}

	@Override
	public Value visit(BoolExp e, Env env) {
		return new BoolVal(e.v());
	}

	@Override
	public Value visit(DivExp e, Env env) {
		List<Exp> operands = e.all();
		NumVal lVal = (NumVal) operands.get(0).accept(this, env);
		double result = lVal.v(); 
		for(int i=1; i<operands.size(); i++) {
			NumVal rVal = (NumVal) operands.get(i).accept(this, env);
			result = result / rVal.v();
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(MultExp e, Env env) {
		List<Exp> operands = e.all();
		double result = 1;
		for(Exp exp: operands) {
			NumVal intermediate = (NumVal) exp.accept(this, env); // Dynamic type-checking
			result *= intermediate.v(); //Semantics of MultExp.
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(Program p, Env env) {
		try {
			for(DefineDecl d: p.decls())
				d.accept(this, initEnv);
			return (Value) p.e().accept(this, initEnv);
		} catch (ClassCastException e) {
			return new DynamicError(e.getMessage());
		}
	}

	@Override
	public Value visit(SubExp e, Env env) {
		List<Exp> operands = e.all();
		NumVal lVal = (NumVal) operands.get(0).accept(this, env);
		double result = lVal.v();
		for(int i=1; i<operands.size(); i++) {
			NumVal rVal = (NumVal) operands.get(i).accept(this, env);
			result = result - rVal.v();
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(VarExp e, Env env) {
		// Previously, all variables had value 42. New semantics.
		return env.get(e.name());
	}	

	@Override
	public Value visit(LetExp e, Env env) { // New for varlang.
		List<String> names = e.names();
		List<Exp> value_exps = e.value_exps();
		List<Value> values = new ArrayList<Value>(value_exps.size());
		
		for(Exp exp : value_exps) 
			values.add((Value)exp.accept(this, env));
		
		Env new_env = env;
		for (int index = 0; index < names.size(); index++)
			new_env = new ExtendEnv(new_env, names.get(index), values.get(index));

		return (Value) e.body().accept(this, new_env);		
	}	
	
	@Override
	public Value visit(DefineDecl e, Env env) { // New for definelang.
		String name = e.name();
		Exp value_exp = e.value_exp();
		Value value = (Value) value_exp.accept(this, env);
		((GlobalEnv) initEnv).extend(name, value);
		return new Value.UnitVal();		
	}	

	@Override
	public Value visit(LambdaExp e, Env env) { // New for funclang.
		if(e.defParam()!=null){
				env = new ExtendEnv(env, e.formals().get(e.formals().size() - 1),
						(Value) e.defParam().accept(this, env));
			}
		return new Value.FunVal(env, e.formals(), e.body());
	}
	
	@Override
	public Value visit(CallExp e, Env env) { // New for funclang.
		Object result = e.operator().accept(this, env);
		if(!(result instanceof Value.FunVal))
			return new Value.DynamicError("Operator not a function in call " +  ts.visit(e, env));
		Value.FunVal operator =  (Value.FunVal) result; //Dynamic checking
		List<Exp> operands = e.operands();

		// Call-by-value semantics
		List<Value> actuals = new ArrayList<Value>(operands.size());
		for(Exp exp : operands) 
			actuals.add((Value)exp.accept(this, env));
		
		List<String> formals = operator.formals();
		int formaloffset=0;
		if (formals.size()-1 == actuals.size()){
			//if default parameter is not overriden
			formaloffset=1;
		}
		else if (formals.size()!=actuals.size())
			return new Value.DynamicError("Argument mismatch in call " + ts.visit(e, env));

		Env fun_env = operator.env();
		for (int index = 0; index < formals.size()-formaloffset; index++)
			fun_env = new ExtendEnv(fun_env, formals.get(index), actuals.get(index));
		
		return (Value) operator.body().accept(this, fun_env);
	}	
	
	@Override
	public Value visit(IfExp e, Env env) { // New for funclang.
		Object result = e.conditional().accept(this, env);
		if(!(result instanceof Value.BoolVal))
			return new Value.DynamicError("Condition not a boolean in expression " +  ts.visit(e, env));
		Value.BoolVal condition =  (Value.BoolVal) result; //Dynamic checking
		
		if(condition.v())
			return (Value) e.then_exp().accept(this, env);
		else return (Value) e.else_exp().accept(this, env);
	}

	@Override
	public Value visit(LessExp e, Env env) { // New for funclang.
		Value.NumVal first = (Value.NumVal) e.first_exp().accept(this, env);
		Value.NumVal second = (Value.NumVal) e.second_exp().accept(this, env);
		return new Value.BoolVal(first.v() < second.v());
	}
	
	@Override
	public Value visit(EqualExp e, Env env) { // New for funclang.
		Value first = (Value) e.first_exp().accept(this, env);
		Value second = (Value) e.second_exp().accept(this, env);
		return new Value.BoolVal(compareValues(first,second));
	}

	@Override
	public Value visit(GreaterExp e, Env env) { // New for funclang.
		Value.NumVal first = (Value.NumVal) e.first_exp().accept(this, env);
		Value.NumVal second = (Value.NumVal) e.second_exp().accept(this, env);
		return new Value.BoolVal(first.v() > second.v());
	}
	
	@Override
	public Value visit(CarExp e, Env env) { 
		Value.PairVal pair = (Value.PairVal) e.arg().accept(this, env);
		return pair.fst();
	}
	
	@Override
	public Value visit(CdrExp e, Env env) { 
		Value.PairVal pair = (Value.PairVal) e.arg().accept(this, env);
		return pair.snd();
	}
	
	@Override
	public Value visit(ConsExp e, Env env) { 
		Value first = (Value) e.fst().accept(this, env);
		Value second = (Value) e.snd().accept(this, env);
		return new Value.PairVal(first, second);
	}

	@Override
	public Value visit(ListExp e, Env env) { // New for funclang.
		List<Exp> elemExps = e.elems();
		int length = elemExps.size();
		if(length == 0)
			return new Value.Null();
		
		//Order of evaluation: left to right e.g. (list (+ 3 4) (+ 5 4)) 
		Value[] elems = new Value[length];
		for(int i=0; i<length; i++)
			elems[i] = (Value) elemExps.get(i).accept(this, env);
		
		Value result = new Value.Null();
		for(int i=length-1; i>=0; i--) 
			result = new PairVal(elems[i], result);
		return result;
	}	
	
	@Override
	public Value visit(NullExp e, Env env) {
		Value val = (Value) e.arg().accept(this, env);
		return new BoolVal(val instanceof Value.Null);
	}

	@Override
	public Value visit(NumPredExp e, Env env) {
		Value val = (Value) e.exp().accept(this, env);
		return new BoolVal(val instanceof Value.NumVal);
	}

	@Override
	public Value visit(BoolPredExp e, Env env) {
		Value val = (Value) e.exp().accept(this, env);
		return new BoolVal(val instanceof Value.BoolVal);
	}

	@Override
	public Value visit(StrPredExp e, Env env) {
		Value val = (Value) e.exp().accept(this, env);
		return new BoolVal(val instanceof Value.StringVal);
	}

	@Override
	public Value visit(ProcPredExp e, Env env) {
		Value val = (Value) e.exp().accept(this, env);
		return new BoolVal(val instanceof Value.FunVal);
	}

	@Override
	public Value visit(PairPredExp e, Env env) {
		Value val = (Value) e.exp().accept(this, env);

		return new BoolVal(val instanceof Value.PairVal);
	}

	@Override
	public Value visit(ListPredExp e, Env env) {
		Object val = e.exp().accept(this, env);
		//take outer value of expression
		if(val instanceof PairVal){
			//value is a pair, begin unwrapping data structure
			Object snd = ((PairVal) val).snd();
			if (snd instanceof PairVal){
				if(((PairVal) snd).snd() instanceof PairVal){
					//we have unwrapped the pair three times, so it is a list
					// as it has more than 2 elements
					return new BoolVal(true);
				}
				//third unwrap does not reveal another PairVal, so val
				//is simply a normal PairVal
				else return new BoolVal(false);

			}
			//handle conditions such as (list? (cons 1 (list)))
			else if (snd instanceof Null){
				return new BoolVal(true);
			}

		}
		else if(val instanceof Null){
			return new BoolVal(true);
		}
		if (e.exp() instanceof ListExp) {
			return new BoolVal(true);
		}
		return new BoolVal(false);
	}

	@Override
	public Value visit(UnitPredExp e, Env env){
		Value val = (Value) e.exp().accept(this, env);
		return new BoolVal(val instanceof UnitVal);
	}

	@Override
	public Value visit(RefExp e, Env env) {
		return heap.ref((Value) e.val_exp().accept(this,env));
	}

	@Override
	public Value visit(DerefExp e, Env env) {
		Exp loc_exp = e.loc_exp();
		return heap.deref((RefVal) loc_exp.accept(this,env));
	}

	@Override
	public Value visit(SetrefExp e, Env env) {
		Exp loc_exp = e.loc_exp();
		Exp val_exp = e.val_exp();
		return heap.setref((RefVal)loc_exp.accept(this,env), (Value)val_exp.accept(this,env));
	}

	@Override
	public Value visit(FreeExp e, Env env) {
		heap.free((RefVal)e.loc_exp().accept(this,env));
		return new UnitVal();
	}

	public Value visit(EvalExp e, Env env) {
		StringVal programText = (StringVal) e.code().accept(this, env);
		Program p = _reader.parse(programText.v());
		return (Value) p.accept(this, env);
	}

	public Value visit(ReadExp e, Env env) {
		StringVal fileName = (StringVal) e.file().accept(this, env);
		try {
			String text = Reader.readFile("" + System.getProperty("user.dir") + File.separator + fileName.v());
			return new StringVal(text);
		} catch (IOException ex) {
			return new DynamicError(ex.getMessage());
		}
	}

	@Override
	public Value visit(ArrayExp e, Env env) {
		List<Exp> dims = e.dims();
		List<Integer> dim_list = new ArrayList<>();
		int total_nums = 1;
		if(dims.size() == 0){
			total_nums = 0;
		}
		for(Exp exp: dims){
			Value val = (Value)exp.accept(this , env);
			if(val instanceof DynamicError){
				return val;
			}
			if(!(val instanceof NumVal) || ((NumVal)val).v() != Math.floor(((NumVal)val).v())){
				return new DynamicError("Error: Array sizes are not ints.");
			}
			int dim = (int)((NumVal)val).v();
			if(dim <= 0){
				return new DynamicError("Error: Array sizes cannot be negative.");
			}
			dim_list.add(dim);
			total_nums *= dim;
		}
		List<RefVal> r_vals = new ArrayList<>();
		for(int i = 0; i < total_nums; i++){
			Value res = heap. ref(new NumVal(0));
			if(res instanceof DynamicError){
				return res;
			}
			r_vals.add((RefVal)res);
		}
		return new ArrayVal(dim_list, r_vals ,heap);
	}
	@Override
	public Value visit(IndexExp e, Env env) {
		List<Exp> indices = e.indices();
		Exp array = e.array();
		Value arr = (Value)array.accept(this , env);
		if(! (arr instanceof ArrayVal)){
			return new DynamicError("Error: First argument must be an array.");
		}
		if(((ArrayVal)arr).getDimentions(). size() == 0){
			return new DynamicError("Error: An empty array has no elements to index.");
		}
		List<Integer> requestedIndex = new ArrayList<>();
		int i = 0;
		for(Exp exp: indices){
			Value val = (Value)exp.accept(this , env);
			if(val instanceof DynamicError){
				return val;
			}
			if(!(val instanceof NumVal) || ((NumVal)val).v() != Math. floor(((NumVal)val).v()) || ((int) ((NumVal)val
			).v() <= 0)){
				return new DynamicError("Error: Array indices should be positive integers.");
			}
			int index = (int) ((NumVal)val).v();
			if(index > ((ArrayVal)arr).getDimentions().get(i )){
				return new DynamicError("Error: Indices should not exceed their relevant array size.");
			}
			requestedIndex.add(index);
			i++;
		}
		i = 0;
//Now, using indicesEval , compute the index we want to access!
		if(requestedIndex. size() != ((ArrayVal)arr).getDimentions(). size()){
			return new DynamicError("Error: Too many or too few indices for this array.");
		}
		int index = getIndex(requestedIndex, ((ArrayVal)arr).getDimentions());
		return heap.deref(((ArrayVal)arr).getVals().get(index));
	}
	@Override
	public Value visit(ArrAssignExp e, Env env) {
		List<Exp> indices = e.indices();
		Exp array = e.getArr();
		Value arr = (Value)array.accept(this , env);
		if(arr instanceof DynamicError){
			return arr;
		}
		if(! (arr instanceof ArrayVal)){
			return new DynamicError("Error: First argument must be an array.");
		}
		if(((ArrayVal)arr).getDimentions(). size() == 0){
			return new DynamicError("Error: An empty array has no elements to index.");
		}
		List<Integer> indicesEval = new ArrayList<>();
		int i = 0;
		for(Exp exp: indices){
			Value val = (Value)exp.accept(this , env);
			if(val instanceof DynamicError){
				return val;
			}
			if(!(val instanceof NumVal) || ((NumVal)val).v() != Math. floor(((NumVal)val).v())){
				return new DynamicError("Error: Array indices should be integers.");
			}
			int index = ((Double)((NumVal)val).v()).intValue();
			if(index<= 0){
				return new DynamicError("Error: Indices must be positive.");
			}
			if(index > ((ArrayVal)arr).getDimentions().get(i )){
				return new DynamicError("Error: Indices should not exceed their relevant array size.");
			}
			indicesEval.add(index);
			i++;
		}
//Now, using indicesEval , compute the index we want to access!
		if(indicesEval. size() != ((ArrayVal)arr).getDimentions(). size()){
			return new DynamicError("Error: Too many or too few indices for this array.");
		}
		List<Integer> dims = ((ArrayVal)arr).getDimentions();
		int index = getIndex(indicesEval, dims);
//Check the new value
		Value toChange = (Value)e.val().accept(this , env);
		if(toChange instanceof DynamicError){
			return toChange;
		}
		if(!(toChange instanceof NumVal)){
			return new DynamicError("Error: Current functionality allows only numeric arrays.");
		}
//Update the reference and return the array.
		heap. setref(((ArrayVal)arr).getVals().get(index) ,toChange);
		return arr;
	}
	//private helper to transform a l is t of indices into the index of the 1D RefVal array.
	private int getIndex(List<Integer> indicesEval, List<Integer> dims) {
//First , for a one dimensional array, the index is just the index we found (−1 because 0 indexing) .
		if (indicesEval.size() == 1) {
			return indicesEval.get(0) −1;
		}
//Due to the way this homework is set up, we always have to add the f irst coordinate and the colNum∗
		secondCoordinate.
		int index = (indicesEval.get(1) −1)+(indicesEval.get(0) −1) ∗(dims.get(1));
//The remainder of the coordinates are multiplied by a factor based on the arr dimensions.
		for (int i = 2; i < indicesEval.size(); i++) {
			int multFactor = 1;
			for (int j = 0; j < i; j++) {
				multFactor ∗=dims.get(j);
			}
			index += (indicesEval.get(i) −1) ∗multFactor;
		}
		return index;
	}
	private Env initialEnv() {
		GlobalEnv initEnv = new GlobalEnv();
		
		/* Procedure: (read <filename>). Following is same as (define read (lambda (file) (read file))) */
		List<String> formals = new ArrayList<>();
		formals.add("file");
		Exp body = new AST.ReadExp(new VarExp("file"));
		Value.FunVal readFun = new Value.FunVal(initEnv, formals, body);
		initEnv.extend("read", readFun);

		/* Procedure: (require <filename>). Following is same as (define require (lambda (file) (eval (read file)))) */
		formals = new ArrayList<>();
		formals.add("file");
		body = new EvalExp(new AST.ReadExp(new VarExp("file")));
		Value.FunVal requireFun = new Value.FunVal(initEnv, formals, body);
		initEnv.extend("require", requireFun);
		
		/* Add new built-in procedures here */ 
		
		return initEnv;
	}
	
	Reader _reader; 
	public Evaluator(Reader reader) {
		_reader = reader;
	}
}
