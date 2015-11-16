public class test{

 public static void main (String args[]){
   if(args[0]==null){
     System.out.println("Please specify a filename (e.g. java test pascal2.ebnf");
     System.exit(0);
   }
  PredictionMaker predict = new PredictionMaker(args[0]);
  System.out.println("\n\n"+predict.toString() );
 }
}