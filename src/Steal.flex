/*
 *
 * Vivek Kumar, (vk.aries@gmail.com)
 * http://vivkumar.github.io/
 * February 2016
 *
 * ----------------------------------------------------------------------------------------
 * This code is writen as per the description
 * provided in the following paper:
 *
 * "Work-stealing without the baggage"
 * Vivek Kumar, Daniel Frampton, Steve Blackburn, David Grove, and Olivier Tardieu:
 * In Proceedings of the ACM International Conference on Object Oriented Programming Systems 
 * Languages and Applications, OOPSLA '12, pages 297–314, New York, NY, USA, 2012
 * doi: 10.1145/2384616.2384639
 *
 * ----------------------------------------------------------------------------------------
 *
 */
<YYINITIAL> {
  "async"                     { return sym(Terminals.STEAL); }
  "finish"                 { return sym(Terminals.SYNCSTEAL); }
}
