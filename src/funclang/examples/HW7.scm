//Transcript #5a
$ (deref (ref 1))
1
$

$ (free (ref 1))
$

$ (let ((loc (ref 1))) (set! loc 2))
2
$

$ (let ((loc (ref 3))) (set! loc (deref loc)))
3
$