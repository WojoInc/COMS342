package funclang;

import java.util.List;

import funclang.AST.Exp;

public interface Value {
	public String tostring();
	static class FunVal implements Value { //New in the funclang
		private Env _env;
		private List<String> _formals;
		private Exp _body;
		public FunVal(Env env, List<String> formals, Exp body) {
			_env = env;
			_formals = formals;
			_body = body;
		}
		public Env env() { return _env; }
		public List<String> formals() { return _formals; }
		public Exp body() { return _body; }
	    public String tostring() { 
			String result = "(lambda ( ";

			if(_env.get(_formals.get(formals().size()-1))!=null){
				int i;
				for(i=0; i<_formals.size()-1; i++){
					result+=_formals.get(i) + " ";
				}
				result+= "(" + _formals.get(i) +" = " +
						((NumVal) _env.get(_formals.get(formals().size()-1))).v() + ")";
			}
			else {
				for (String formal : _formals)
					result += formal + " ";
			}
			result += ") ";
			result += _body.accept(new Printer.Formatter(), _env);
			return result + ")";
	    }
	}
	static class NumVal implements Value {
	    private double _val;
	    public NumVal(double v) { _val = v; } 
	    public double v() { return _val; }
	    public String tostring() { 
	    	int tmp = (int) _val;
	    	if(tmp == _val) return "" + tmp;
	    	return "" + _val; 
	    }
	}
	static class BoolVal implements Value {
		private boolean _val;
	    public BoolVal(boolean v) { _val = v; } 
	    public boolean v() { return _val; }
	    public String tostring() { if(_val) return "#t"; return "#f"; }
	}
	static class StringVal implements Value {
		private java.lang.String _val;
	    public StringVal(String v) { _val = v; } 
	    public String v() { return _val; }
	    public java.lang.String tostring() { return "" + _val; }
	}
	static class PairVal implements Value {
		protected Value _fst;
		protected Value _snd;
	    public PairVal(Value fst, Value snd) { _fst = fst; _snd = snd; } 
		public Value fst() { return _fst; }
		public Value snd() { return _snd; }
	    public java.lang.String tostring() { 
	    	if(isList()) return listToString();
	    	return "(" + _fst.tostring() + " " + _snd.tostring() + ")"; 
	    }
	    private boolean isList() {
	    	if(_snd instanceof Value.Null) return true;
	    	if(_snd instanceof Value.PairVal &&
	    		((Value.PairVal) _snd).isList()) return true;
	    	return false;
	    }
	    private java.lang.String listToString() {
	    	String result = "(";
	    	result += _fst.tostring();
	    	Value next = _snd; 
	    	while(!(next instanceof Value.Null)) {
	    		result += " " + ((PairVal) next)._fst.tostring();
	    		next = ((PairVal) next)._snd;
	    	}
	    	return result + ")";
	    }
	}
	static class Null implements Value {
		public Null() {}
	    public String tostring() { return "()"; }
	}
	static class UnitVal implements Value {
		public static final UnitVal v = new UnitVal();
	    public String tostring() { return ""; }
	}
	static class RefVal implements Value {
		private int loc = -1;
		public RefVal(int loc){
			this.loc = loc;
		}
		public int loc(){return loc;}
		@Override
		public String tostring() {
			return "loc: " + this.loc;
		}
	}

	static class ArrayVal implements Value {
		private List<Integer> _dims;
		private List<RefVal> _vals;
		private Heap _heap;

		public ArrayVal(List<Integer> _dims, List<RefVal> _vals, Heap _heap) {
			this._dims = _dims;
			this._vals = _vals;
			this._heap = _heap;
		}

		public List<Integer> getDims() {
			return _dims;
		}

		public List<RefVal> getVals() {
			return _vals;
		}

		@Override
		public String tostring() {
			int index = 0;
			StringBuilder res = new StringBuilder();
			if (_dims.size() == 0) {
				return "[]";
			}
			if (_dims.size() == 1) {
				res.append("[");
				for (int i = 0; i < _vals.size() - 1; i++) {
					res.append(_heap.deref(_vals.get(i)).tostring());
					res.append("\n");
				}
				res.append(_heap.deref(_vals.get(_vals.size() - 1)).tostring());
				res.append("]");
				return res.toString();
			}
			res = arrStringBuilder(_dims.size() - 1, res);
			return res.toString();
		}

		private StringBuilder arrStringBuilder(int dimIndex, StringBuilder str) {
			if (dimIndex == 1) {
				str.append("[");
				for (int i = 0; i < _dims.get(0); i++) {
					str.append("[");
					for (int j = 0; j < _dims.get(1); j++) {
						str.append(_heap.deref(_vals.get(index)).tostring());
						str.append(" ");
						index++;
					}
					str.delete(str.length() - 1, str.length());
					str.append("]\n");
				}
				str.delete(str.length() - 2, str.length());
				str.append("]");
				str.append("]");
				return str;
			}

			str.append("[");
			for (int i = 0; i < _dims.get(dimIndex); i++) {
				arrStringBuilder(dimIndex - 1, str);
				str.append("\n");
			}
//Remove the last new line
			str.delete(str.length() - 1, str.length());
			str.append("]");
			return str;
		}
	}

	static class DynamicError implements Value { 
		private String message = "Unknown dynamic error.";
		public DynamicError(String message) { this.message = message; }
	    public String tostring() { return "" + message; }
	}
}
