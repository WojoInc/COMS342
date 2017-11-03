grammar FuncLang;
 
import ListLang; //Import all rules from ListLang grammar.
 
 // New elements in the Grammar of this Programming Language
 //  - grammar rules start with lowercase

 exp returns [Exp ast]: 
		va=varexp { $ast = $va.ast; }
		| num=numexp { $ast = $num.ast; }
		| str=strexp { $ast = $str.ast; }
		| bl=boolexp { $ast = $bl.ast; }
        | add=addexp { $ast = $add.ast; }
        | sub=subexp { $ast = $sub.ast; }
        | mul=multexp { $ast = $mul.ast; }
        | div=divexp { $ast = $div.ast; }
        | let=letexp { $ast = $let.ast; }
        | lam=lambdaexp { $ast = $lam.ast; }
        | call=callexp { $ast = $call.ast; }
        | i=ifexp { $ast = $i.ast; }
        | less=lessexp { $ast = $less.ast; }
        | eq=equalexp { $ast = $eq.ast; }
        | gt=greaterexp { $ast = $gt.ast; }
        | car=carexp { $ast = $car.ast; }
        | cdr=cdrexp { $ast = $cdr.ast; }
        | cons=consexp { $ast = $cons.ast; }
        | list=listexp { $ast = $list.ast; }
        | nl=nullexp { $ast = $nl.ast; }
        | npd=numpredexp { $ast = $npd.ast; }
        | bpd=boolpredexp { $ast = $bpd.ast; }
        | spd=strpredexp { $ast = $spd.ast; }
        | prpd=procpredexp { $ast = $prpd.ast; }
        | papd=pairpredexp { $ast = $papd.ast; }
        | lpd=listpredexp { $ast = $lpd.ast; }
        | upd=unitpredexp { $ast = $upd.ast; }
        | ref=refexp { $ast = $ref.ast; }
        | srf=setrefexp { $ast = $srf.ast; }
        | drf=derefexp { $ast = $drf.ast; }
        | fre=freeexp { $ast = $fre.ast; }
        | arr=arrayexp { $ast = $arr.ast; }
        | idx=indexexp { $ast = $idx.ast; }
        | arras=arrayassignexp { $ast = $arras.ast; }
        ;

 lambdaexp returns [LambdaExp ast] 
        locals [ArrayList<String> formals ]
 		@init { $formals = new ArrayList<String>(); } :
 		'(' Lambda 
 			'(' (id=Identifier { $formals.add($id.text); } )* ')'
 			body=exp
 		')' { $ast = new LambdaExp($formals, $body.ast, null); } |
 		'(' Lambda
         	'(' (id=Identifier { $formals.add($id.text); } )*
         	'(' (id=Identifier Equal num=Number { $formals.add($id.text); }) ')'')'
         	body=exp
         	')' { $ast = new LambdaExp($formals, $body.ast, new NumExp(Integer.parseInt($num.text))); }
 		;

 callexp returns [CallExp ast] 
        locals [ArrayList<Exp> arguments = new ArrayList<Exp>();  ] :
 		'(' f=exp 
 			( e=exp { $arguments.add($e.ast); } )* 
 		')' { $ast = new CallExp($f.ast,$arguments); }
 		;

 ifexp returns [IfExp ast] :
 		'(' If 
 		    e1=exp 
 			e2=exp 
 			e3=exp 
 		')' { $ast = new IfExp($e1.ast,$e2.ast,$e3.ast); }
 		;

 lessexp returns [LessExp ast] :
 		'(' Less 
 		    e1=exp 
 			e2=exp 
 		')' { $ast = new LessExp($e1.ast,$e2.ast); }
 		;

 equalexp returns [EqualExp ast] :
 		'(' Equal e1=exp e2=exp ')' { $ast = new EqualExp($e1.ast,$e2.ast); }
 		//| '(' Equal e1=boolexp e2=boolexp ')' { $ast = new EqualExp($e1.ast,$e2.ast); }
 		;

 greaterexp returns [GreaterExp ast] :
 		'(' Greater 
 		    e1=exp 
 			e2=exp 
 		')' { $ast = new GreaterExp($e1.ast,$e2.ast); }
 		;
    //begin additional predicate expressions
 numpredexp returns [NumPredExp ast] :
        '(' Numpred e=exp ')' { $ast = new NumPredExp($e.ast); }
        ;
 boolpredexp returns [BoolPredExp ast] :
        '(' Boolpred e=exp ')' { $ast = new BoolPredExp($e.ast); }
        ;
 strpredexp returns [StrPredExp ast] :
        '(' Stringpred e=exp ')' { $ast = new StrPredExp($e.ast); }
        ;
 procpredexp returns [ProcPredExp ast] :
        '(' Procedpred e=exp ')' { $ast = new ProcPredExp($e.ast); }
        ;
 pairpredexp returns [PairPredExp ast] :
         '(' Pairpred e=exp ')' { $ast = new PairPredExp($e.ast); }
         ;
 listpredexp returns [ListPredExp ast] :
         '(' Listpred e=exp ')' { $ast = new ListPredExp($e.ast); }
         ;
 unitpredexp returns [UnitPredExp ast] :
        '(' Unitpred e=exp ')' { $ast = new UnitPredExp($e.ast); }
        ;
 refexp returns [RefExp ast] :
        '(' Ref e=exp ')' { $ast = new RefExp($e.ast); }
        ;
 derefexp returns [DerefExp ast] :
         '(' Deref e=exp ')' { $ast = new DerefExp($e.ast); }
        ;
 setrefexp returns [SetrefExp ast] :
         '(' Setref e1=exp e2=exp ')' { $ast = new SetrefExp($e1.ast, $e2.ast); }
        ;
 freeexp returns [FreeExp ast] :
         '(' Free e=exp ')' { $ast = new FreeExp($e.ast); }
        ;
 arrayexp returns [ArrayExp ast]
 locals [ArrayList<Exp> dims = new ArrayList<Exp>(); ]:
        '(' 'array' ( e=exp { $dims.add($e.ast); } ) ∗
        ')' { $ast = new ArrayExp($dims); }
        ;
 indexexp returns [IndexExp ast]
 locals [ArrayList<Exp> idxs = new ArrayList<Exp>(); ]:
        '(' 'index' arr=exp ( e=exp { $idxs.add($e.ast); } ) +
        ')' { $ast = new IndexExp($arr.ast, $idxs); }
        ;
 arrayassignexp returns [ArrAssignExp ast]
 locals [ArrayList<Exp> idxs = new ArrayList<Exp>(); ]:
        '(' 'assign' arr=exp ( e=exp { $idxs.add($e.ast); } ) +
         _val=exp
        ')' { $ast = new ArrAssignExp($arr.ast, $idxs, $_val.ast); }
        ;

Numpred : 'number?';
Boolpred : 'boolean?';
Stringpred : 'string?';
Procedpred : 'procedure?';
Pairpred : 'pair?';
Listpred : 'list?';
Unitpred : 'unit?';
Ref : 'ref';
Deref : 'deref';
Setref : 'set!';
Free : 'free';
