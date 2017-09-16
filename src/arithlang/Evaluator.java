package arithlang;

import static arithlang.AST.*;
import static arithlang.Value.*;

import java.io.IOException;
import java.util.List;

public class Evaluator implements Visitor<Value> {
    NumVal memory;
    Printer.Formatter ts = new Printer.Formatter();


    Evaluator() {
        //So the evaluator will not throw a null pointer exception in the event that there
        // has not been anything stored yet. Should simply return 0.
        memory = new NumVal(0);
    }
    Value valueOf(Program p) {
        // Value of a program in this language is the value of the expression
        return (Value) p.accept(this);
    }

    @Override
    public Value visit(AddExp e) {
        List<Exp> operands = e.all();
        double result = 0;
        for (Exp exp : operands) {
            NumVal intermediate = (NumVal) exp.accept(this); // Dynamic type-checking
            result += intermediate.v(); //Semantics of AddExp in terms of the target language.
        }
        return new NumVal(result);
    }

    @Override
    public Value visit(NumExp e) {
        return new NumVal(e.v());
    }

    @Override
    public Value visit(DivExp e) {
        List<Exp> operands = e.all();
        NumVal lVal = (NumVal) operands.get(0).accept(this);
        double result = lVal.v();
        for (int i = 1; i < operands.size(); i++) {
            NumVal rVal = (NumVal) operands.get(i).accept(this);
            result = result / rVal.v();
        }
        return new NumVal(result);
    }

    @Override
    public Value visit(MultExp e) {
        List<Exp> operands = e.all();
        double result = 1;
        for (Exp exp : operands) {
            NumVal intermediate = (NumVal) exp.accept(this); // Dynamic type-checking
            result *= intermediate.v(); //Semantics of MultExp.
        }
        return new NumVal(result);
    }

    @Override
    public Value visit(Program p) {
        return (Value) p.e().accept(this);
    }

    @Override
    public Value visit(PrimExp e) {
        List<Exp> operands = e.all();
        NumVal intermediate = (NumVal) operands.get(0).accept(this); // Dynamic type-checking
        if (intermediate.v() < 0) {
            System.out.println("prime operand should not be negative");
        }
        if (intermediate.v() <= 1) {
            return new NumVal(0);
        } else if (intermediate.v() <= 3) {
            return new NumVal(1);
        } else if (((intermediate.v() % 2) == 0) || (intermediate.v() % 3 == 0)) {
            return new NumVal(0);
        }
        int i = 5;
        while ((i * i) <= intermediate.v()) {
            if (((intermediate.v() % i) == 0) || ((intermediate.v() % (i + 2)) == 0)) {
                return new NumVal(0);
            }
            i += 6;
        }
        return new NumVal(1);
    }

    @Override
    public Value visit(MrecExp e) {
        return memory;
    }

    @Override
    public Value visit(MclrExp e) {
        memory = new NumVal(0);
        return memory;
    }

    @Override
    public Value visit(MaddExp e) {
        List<Exp> operands = e.all();
        double result = memory.v();
        for (Exp exp : operands) {
            NumVal intermediate = (NumVal) exp.accept(this); // Dynamic type-checking
            result += intermediate.v(); //Semantics of AddExp in terms of the target language.
        }
        memory = new NumVal(result);
        //book was not clear about what this should return, I just assumed return current value of memory
        //so this expression can be chained with others.
        return memory;
    }

    @Override
    public Value visit(MsubExp e) {
        List<Exp> operands = e.all();
        double result = memory.v();
        for (Exp exp : operands) {
            NumVal intermediate = (NumVal) exp.accept(this); // Dynamic type-checking
            result -= intermediate.v(); //Semantics of AddExp in terms of the target language.
        }
        memory = new NumVal(result);
        //book was also not clear about what this should return, I just assumed return current value of memory
        //so this expression can be chained with others.
        return memory;
    }

    @Override
    public Value visit(SubExp e) {
        List<Exp> operands = e.all();
        NumVal lVal = (NumVal) operands.get(0).accept(this);
        double result = lVal.v();
        for (int i = 1; i < operands.size(); i++) {
            NumVal rVal = (NumVal) operands.get(i).accept(this);
            result = result - rVal.v();
        }
        return new NumVal(result);
    }

}
