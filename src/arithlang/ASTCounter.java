package arithlang;

import java.util.List;

import static arithlang.AST.*;
import static arithlang.Value.*;

public class ASTCounter implements Visitor<Integer> {

    public void printCount(Program p) {
        System.out.println(p.accept(this));
    }

    @Override
    public Integer visit(NumExp e) {
        return 1;
    }

    @Override
    public Integer visit(AddExp e) {
        List<Exp> operands = e.all();
        Integer result = 1;
        for (Exp exp :
                operands) {
            Integer intermediate = (Integer) exp.accept(this);
            result += intermediate;
        }
        return result;
    }

    @Override
    public Integer visit(SubExp e) {
        List<Exp> operands = e.all();
        Integer result = 1;
        for (Exp exp :
                operands) {
            Integer intermediate = (Integer) exp.accept(this);
            result += intermediate;
        }
        return result;
    }

    @Override
    public Integer visit(MultExp e) {
        List<Exp> operands = e.all();
        Integer result = 1;
        for (Exp exp :
                operands) {
            Integer intermediate = (Integer) exp.accept(this);
            result += intermediate;
        }
        return result;
    }

    @Override
    public Integer visit(DivExp e) {
        List<Exp> operands = e.all();
        Integer result = 1;
        for (Exp exp :
                operands) {
            Integer intermediate = (Integer) exp.accept(this);
            result += intermediate;
        }
        return result;
    }

    @Override
    public Integer visit(Program p) {
        return (Integer) p.e().accept(this) + 1;
    }

    @Override
    public Integer visit(PrimExp e) {
        return (Integer) e.accept(this) + 1;
    }

    @Override
    public Integer visit(MrecExp e) {
        return 1;
    }

    @Override
    public Integer visit(MclrExp e) {
        return 1;
    }

    @Override
    public Integer visit(MaddExp e) {
        List<Exp> operands = e.all();
        Integer result = 1;
        for (Exp exp :
                operands) {
            Integer intermediate = (Integer) exp.accept(this);
            result += intermediate;
        }
        return result;
    }

    public Integer visit(MsubExp e) {
        List<Exp> operands = e.all();
        Integer result = 1;
        for (Exp exp :
                operands) {
            Integer intermediate = (Integer) exp.accept(this);
            result += intermediate;
        }
        return result;
    }
}
