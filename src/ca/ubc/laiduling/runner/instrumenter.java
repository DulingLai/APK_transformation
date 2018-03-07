package ca.ubc.laiduling.runner;

import ca.ubc.laiduling.data.Constants;
import ca.ubc.laiduling.util.Utils;


public class instrumenter {


    public static void main(String[] args) {

        // Step 1. decompile apk
        if(!Utils.decompileApkFile(Constants.APP_NAME)) return;

        // Step 2 & 3. collect and iterate all class files
        Utils.iterateClassFiles(Constants.APP_NAME);

        // Step 4. Pack modified class files into jar file
        if(!Utils.packClassFiles(Constants.APP_NAME)) return;

        // Step 5. convert the jar file to dex file
        if(!Utils.jar2Dex(Constants.INSTRUMENTED_OUTPUT_DIR + Constants.APP_NAME + ".jar", Constants.INSTRUMENTED_OUTPUT_DIR, Constants.APP_NAME)) return;

        // Step 6. copy the original apk and remove the signature info
        if(!Utils.removeSignatureFromApk(Constants.APP_NAME)) return;

        // Step 7. modify the manifest file
        if(!Utils.modifyManifest(Constants.APP_NAME)) return;

        // Step 8. add dex files to apk
        if(!Utils.addDexToApk("/Users/dulinglai/Library/Android/sdk/build-tools/27.0.3/aapt", Constants.INSTRUMENTED_OUTPUT_DIR, Constants.INSTRUMENTED_OUTPUT_DIR + Constants.APP_NAME + "_unaligned.apk", Constants.APP_NAME)) return;

        // Step 9. align and sign the apk
        if(!Utils.signApk(Constants.APP_NAME)) return;

        // Step 10. deploy the apk on device
        Utils.deployApk(Constants.APP_NAME);
    }
}
