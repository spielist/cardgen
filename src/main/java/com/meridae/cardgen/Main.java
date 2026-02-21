package com.meridae.cardgen;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Main {

    public static void main(String[] args) throws Exception {

        org.mariuszgromada.math.mxparser.License.iConfirmCommercialUse(args[0]);

        log.info("Starting...");

        generateCards(args[1], args[2], Utility.defaultIfMissing(args, 3, args[1]));
//        createCardGrid(args[1], Integer.parseInt(args[2]), args[3]);

        log.info("Finished!");
    }

    public static void generateCards(String inputPath, String defFileName, String outputPath) throws Exception {

        Processor p = new Processor();
        p.processFile(inputPath, defFileName, outputPath);

    }

    public static void createCardGrid(String inputPath, int startCard, String fileName) throws IOException {
        CardGridMaker cgm = new CardGridMaker();
        cgm.makeCardGrid(inputPath, startCard, fileName);
    }

}
