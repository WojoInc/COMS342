package funclang;
import funclang.Value.*;

public interface Heap {
    public Value ref(Value val);
    public Value deref(RefVal r_val);
    public Value setref(RefVal r_val, Value val);
    public Value free(RefVal r_val);
}

class Heap16Bit implements Heap{

    static final int HEAP_SIZE = 65536;

    private Value[] _rep = new Value[HEAP_SIZE];

    int index = 0;

    @Override
    public Value ref(Value val) {
        if(index >= HEAP_SIZE){
            return new DynamicError("<Error: Out of memory>");
        }
        RefVal loc = new RefVal(index);
        _rep[index++] = val;
        return loc;
    }

    @Override
    public Value deref(RefVal loc) {
        try{
            return _rep[loc.loc()];
        }catch (ArrayIndexOutOfBoundsException ex){
            return new DynamicError("<Error: Segmentation Fault at location: " +loc +">");
        }
    }

    @Override
    public Value setref(RefVal loc, Value val) {
        try{
            return _rep[loc.loc()] = val;
        }catch (ArrayIndexOutOfBoundsException ex){
            return new DynamicError("<Error: Segmentation Fault at location: " +loc +">");
        }
    }

    @Override
    public Value free(RefVal loc) {
        try{
            _rep[loc.loc()]=null;
            return loc;
        }catch (ArrayIndexOutOfBoundsException ex){
            return new DynamicError("<Error: Segmentation Fault at location: " +loc +">");
        }
    }
}
