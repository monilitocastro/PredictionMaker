public class test2{

 public static void main (String args[]){
   //if(args[0]==null){
     //System.out.println("Please specify a filename (e.g. java test pascal2.ebnf");
     //System.exit(0);
   //}
  PredictionMaker predict = new PredictionMaker("ruleFour.ebnf");
  System.out.println("\n\n"+predict.toString() );
 }
}