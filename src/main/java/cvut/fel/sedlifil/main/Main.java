package cvut.fel.sedlifil.main;

import java.util.Date;

public class Main {



    public static void main(String[] args) throws Exception {
        String filePath = "/Users/filip/Dropbox/FEL_OI/semestr5/BookSystem/";

        //Path fullPath = Paths.get(FILE_PATH);
        //System.out.println("full " + fullPath.toString());
        //Path abspath = fullPath.toAbsolutePath();
        //System.out.println("abs " + abspath);
        if (args.length == 1){
            filePath = args[0];
        }
        System.out.println("FP: " + filePath);


        long startTime = System.currentTimeMillis();
        ParserClass parserClass = new ParserClass(filePath);

        parserClass.split();

        System.out.println("======BLOCK1==========");
        parserClass.getClassPathWithCuMapBlock1().forEach((K, V) -> System.out.println(K));
        System.out.println("======BLOCK2==========");
        parserClass.getClassPathWithCuMapBlock2().forEach((K, V) -> System.out.println(K));
        System.out.println("======BLOCK3==========");
        parserClass.getClassPathWithCuMapBlock3().forEach((K, V) -> System.out.println(K));
        System.out.println("======REST==========");
        parserClass.getClassPathWithCuMap().forEach((K,V) -> System.out.println(K));

        System.out.println("======TIME==========");
        long elapsedTime = (new Date()).getTime() - startTime;
        System.out.println("Trvani: " + elapsedTime);

        System.out.println("================kjhkjhkjh========");
        FileHandlerParser fhp = new FileHandlerParser(filePath);
        fhp.saveAppToFile(parserClass.getClassPathWithCuMapBlock1(),
                parserClass.getClassPathWithCuMapBlock2(),
                parserClass.getClassPathWithCuMapBlock3(),
                parserClass.getFilesToAllBlock());


    }
}
