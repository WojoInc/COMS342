grammar DefineLang;

import VarLang; //Import all rules from VarLang grammar.
 
 // New elements in the Grammar of this Programming Language
 //  - grammar rules start with lowercase

// We are redefining programs to be zero or more define declarations 
// followed by an optional expression.
 program returns [Program ast]        
 		locals [ArrayList<DefineDecl> defs, Exp expr]
 		@init { $defs = new ArrayList<DefineDecl>(); $expr = new UnitExp(); } :
		(def=definedecl { $defs.add($def.ast); } )* (e=exp { $expr = $e.ast; } )? 
		{ $ast = new Program($defs, $expr); }
		;

 definedecl returns [DefineDecl ast] :
 		'(' Define 
 			id=Identifier
 			e=exp
 			')' { $ast = new DefineDecl($id.text, $e.ast); }
 		;
 		