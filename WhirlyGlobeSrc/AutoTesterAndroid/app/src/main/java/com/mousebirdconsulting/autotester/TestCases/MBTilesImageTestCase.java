package com.mousebirdconsulting.autotester.TestCases;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import com.mousebird.maply.GlobeController;
import com.mousebird.maply.MBTiles;
import com.mousebird.maply.MBTilesImageSource;
import com.mousebird.maply.MapController;
import com.mousebird.maply.MaplyBaseController;
import com.mousebird.maply.QuadImageTileLayer;
import com.mousebirdconsulting.autotester.ConfigOptions;
import com.mousebirdconsulting.autotester.Framework.MaplyTestCase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by sjg on 4/11/16.
 */
public class MBTilesImageTestCase extends MaplyTestCase {

    private static String TAG = "AutoTester";
    private static String MBTILES_DIR = "mbtiles";

    private Activity activity;

    public MBTilesImageTestCase(Activity activity) {
        super(activity);
        setTestName("MBTiles Image Test");
        setDelay(1000);

        this.activity = activity;
    }

    private QuadImageTileLayer setupImageLayer(MaplyBaseController baseController, ConfigOptions.TestType testType) throws Exception
    {

        // We need to copy the file from the asset so that it can be used as a file
        File mbTiles = this.getMbTileFile("mbtiles/geography-class.mbtiles", "geography-class.mbtiles");

        if (!mbTiles.exists()) {
            throw new FileNotFoundException(String.format("Could not copy MBTiles asset to \"%s\"", mbTiles.getAbsolutePath()));
        }

        Log.d(TAG, String.format("Obtained MBTiles SQLLite database \"%s\"", mbTiles.getAbsolutePath()));

        MBTiles mbTilesFile = new MBTiles(mbTiles);
        MBTilesImageSource tileSource = new MBTilesImageSource(mbTilesFile);
        QuadImageTileLayer imageLayer = new QuadImageTileLayer(baseController, tileSource.coordSys, tileSource);
        imageLayer.setCoverPoles(true);
        imageLayer.setHandleEdges(true);

        return imageLayer;
    }

    @Override
    public boolean setUpWithGlobe(GlobeController globeVC) throws Exception {
        globeVC.addLayer(setupImageLayer(globeVC, ConfigOptions.TestType.GlobeTest));
        return true;
    }

    @Override
    public boolean setUpWithMap(MapController mapVC) throws Exception {
        mapVC.addLayer(setupImageLayer(mapVC, ConfigOptions.TestType.MapTest));
        return true;
    }

    private File getMbTileFile(String assetMbTile, String mbTileFilename) throws IOException {

        ContextWrapper wrapper = new ContextWrapper(activity);
        File mbTilesDirectory =  wrapper.getDir(MBTILES_DIR, Context.MODE_PRIVATE);

        InputStream is = activity.getAssets().open(assetMbTile);
        File of = new File(mbTilesDirectory, mbTileFilename);

        if (of.exists()) {
            return of;
        }

        OutputStream os = new FileOutputStream(of);
        byte[] mBuffer = new byte[1024];
        int length;
        while ((length = is.read(mBuffer))>0) {
            os.write(mBuffer, 0, length);
        }
        os.flush();
        os.close();
        is.close();

        return of;

    }




    
}
