/*
 *  Copyright 2017 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.google.android.apps.forscience.whistlepunk.filemetadata;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.apps.forscience.whistlepunk.accounts.AppAccount;
import com.google.android.apps.forscience.whistlepunk.accounts.NonSignedInAccount;
import com.google.android.apps.forscience.whistlepunk.data.nano.GoosciExperimentLibrary;
import com.google.android.apps.forscience.whistlepunk.data.nano.GoosciGadgetInfo;
import com.google.android.apps.forscience.whistlepunk.data.nano.GoosciLocalSyncStatus;
import com.google.android.apps.forscience.whistlepunk.metadata.nano.GoosciExperiment;
import com.google.android.apps.forscience.whistlepunk.metadata.nano.GoosciTrial;
import com.google.android.apps.forscience.whistlepunk.metadata.nano.GoosciUserMetadata;
import com.google.android.apps.forscience.whistlepunk.metadata.nano.Version;
import com.google.protobuf.nano.MessageNano;
import java.io.File;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

/**
 * Tests for the ExperimentCache class. Note: All experiments created should start with prefix
 * "exp_" so that they can be cleaned up automatically.
 */
@RunWith(RobolectricTestRunner.class)
public class ExperimentCacheTest {
  private int mFailureCount = 0;

  private AppAccount appAccount;
  private ExperimentLibraryManager elm;
  private ExperimentCache cache;
  private LocalSyncManager lsm;

  private Context getContext() {
    return RuntimeEnvironment.application.getApplicationContext();
  }

  private ExperimentCache.FailureListener getFailureFailsListener() {
    return new ExperimentCache.FailureListener() {
      @Override
      public void onWriteFailed(Experiment experimentToWrite) {
        throw new RuntimeException("Expected success");
      }

      @Override
      public void onReadFailed(GoosciUserMetadata.ExperimentOverview experimentOverview) {
        throw new RuntimeException("Expected success");
      }

      @Override
      public void onNewerVersionDetected(GoosciUserMetadata.ExperimentOverview experimentOverview) {
        throw new RuntimeException("Expected success");
      }
    };
  }

  private ExperimentCache.FailureListener getFailureExpectedListener() {
    return new ExperimentCache.FailureListener() {
      @Override
      public void onWriteFailed(Experiment experimentToWrite) {
        mFailureCount++;
      }

      @Override
      public void onReadFailed(GoosciUserMetadata.ExperimentOverview localExperimentOverview) {
        mFailureCount++;
      }

      @Override
      public void onNewerVersionDetected(GoosciUserMetadata.ExperimentOverview experimentOverview) {
        mFailureCount++;
      }
    };
  }

  @Before
  public void setUp() {
    cleanUp();
    appAccount = NonSignedInAccount.getInstance(getContext());
    elm = new ExperimentLibraryManager(new GoosciExperimentLibrary.ExperimentLibrary(), appAccount);
    lsm = new LocalSyncManager(new GoosciLocalSyncStatus.LocalSyncStatus(), appAccount);
    cache = new ExperimentCache(getContext(), appAccount, getFailureFailsListener(), 0, elm, lsm);
  }

  @After
  public void tearDown() {
    cleanUp();
  }

  private void cleanUp() {
    File rootDirectory = getContext().getFilesDir();
    for (File file : rootDirectory.listFiles()) {
      if (file.getName().startsWith("exp_")) {
        ExperimentCache.deleteRecursive(file);
      }
    }
    mFailureCount = 0;
  }

  @Test
  public void testExperimentWriteRead() {
    Experiment experiment = Experiment.newExperiment(10, "exp_localId", 0);
    elm.addExperiment(experiment.getExperimentId());
    lsm.addExperiment(experiment.getExperimentId());
    cache.createNewExperiment(experiment);
    cache.writeActiveExperimentFile();

    // Was it set correctly in the ExperimentCache?
    Assert.assertTrue(
        MessageNano.messageNanoEquals(
            cache.getActiveExperimentForTests().getExperimentProto(),
            experiment.getExperimentProto()));

    // Force a load, make sure that's equal too.
    cache.loadActiveExperimentFromFile(experiment.getExperimentOverview());
    assertTrue(
        MessageNano.messageNanoEquals(
            cache.getActiveExperimentForTests().getExperimentProto(),
            experiment.getExperimentProto()));

    // Clean up.
    cache.deleteExperiment("exp_localId");
    assertNull(cache.getActiveExperimentForTests());
  }

  @Test
  public void testExperimentWithChanges() {
    Experiment experiment = Experiment.newExperiment(10, "exp_localId", 0);
    elm.addExperiment(experiment.getExperimentId());
    lsm.addExperiment(experiment.getExperimentId());
    cache.createNewExperiment(experiment);
    assertTrue(cache.needsWrite());

    cache.writeActiveExperimentFile();
    assertFalse(cache.needsWrite());

    experiment.setTitle("Title");
    cache.updateExperiment(experiment, true);
    assertTrue(lsm.getDirty(experiment.getExperimentId()));
    assertEquals(elm.getModified(experiment.getExperimentId()), experiment.getLastUsedTime());
    assertTrue(cache.needsWrite());
    cache.writeActiveExperimentFile();

    // Force a load, make sure that's got the new title.
    cache.loadActiveExperimentFromFile(experiment.getExperimentOverview());
    assertEquals("Title", cache.getActiveExperimentForTests().getTitle());

    // Clean up.
    cache.deleteExperiment("exp_localId");
    assertNull(cache.getActiveExperimentForTests());
  }

  @Test
  public void testChangingExperimentWritesOldOne() {
    Experiment experiment = Experiment.newExperiment(10, "exp_localId", 0);
    elm.addExperiment(experiment.getExperimentId());
    lsm.addExperiment(experiment.getExperimentId());
    cache.createNewExperiment(experiment);
    assertEquals(10, cache.getActiveExperimentForTests().getCreationTimeMs());
    experiment.setTitle("Title");
    cache.updateExperiment(experiment, false);

    Experiment second = Experiment.newExperiment(20, "exp_secondId", 0);
    elm.addExperiment(second.getExperimentId());
    lsm.addExperiment(second.getExperimentId());
    cache.createNewExperiment(second);
    assertEquals(20, cache.getActiveExperimentForTests().getCreationTimeMs());

    cache.getExperiment(experiment.getExperimentOverview());
    assertEquals(10, cache.getActiveExperimentForTests().getCreationTimeMs());
    assertEquals("Title", cache.getActiveExperimentForTests().getTitle());
  }

  @Test
  public void testUpgradeStartsWriteTimer() {
    GoosciExperiment.Experiment proto = createExperimentProto();
    proto.fileVersion.version = 0;
    proto.fileVersion.minorVersion = 0;
    proto.fileVersion.platformVersion = 0;
    cache.upgradeExperimentVersionIfNeeded(
        proto, new GoosciUserMetadata.ExperimentOverview(), 1, 1, 1);
    assertEquals(1, proto.fileVersion.version);
    assertEquals(1, proto.fileVersion.minorVersion);
    assertEquals(1, proto.fileVersion.platformVersion);
    assertEquals(GoosciGadgetInfo.GadgetInfo.Platform.ANDROID, proto.fileVersion.platform);
    assertTrue(cache.needsWrite());
  }

  @Test
  public void testUpgradeWhenVersionMissing() {
    GoosciExperiment.Experiment proto = createExperimentProto();
    proto.fileVersion = null;
    cache.upgradeExperimentVersionIfNeeded(
        proto, new GoosciUserMetadata.ExperimentOverview(), 1, 1, 1);
    assertEquals(1, proto.fileVersion.version);
    assertEquals(1, proto.fileVersion.minorVersion);
    assertEquals(1, proto.fileVersion.platformVersion);
    assertEquals(GoosciGadgetInfo.GadgetInfo.Platform.ANDROID, proto.fileVersion.platform);
  }

  @Test
  public void testNoUpgradeDoesNotStartWriteTimer() {
    GoosciExperiment.Experiment proto = createExperimentProto();
    proto.fileVersion.version = 1;
    proto.fileVersion.minorVersion = 1;
    proto.fileVersion.platformVersion = 1;
    cache.upgradeExperimentVersionIfNeeded(
        proto, new GoosciUserMetadata.ExperimentOverview(), 1, 1, 1);
    assertEquals(1, proto.fileVersion.version);
    assertEquals(1, proto.fileVersion.minorVersion);
    assertEquals(1, proto.fileVersion.platformVersion);
    assertFalse(cache.needsWrite());
  }

  @Test
  public void testVersionTooNewThrowsError() {
    cache =
        new ExperimentCache(getContext(), appAccount, getFailureExpectedListener(), 0, elm, lsm);
    GoosciExperiment.Experiment proto = createExperimentProto();
    proto.fileVersion.version = ExperimentCache.VERSION + 1;
    proto.fileVersion.minorVersion = ExperimentCache.MINOR_VERSION;
    cache.upgradeExperimentVersionIfNeeded(
        proto,
        new GoosciUserMetadata.ExperimentOverview(),
        ExperimentCache.VERSION,
        ExperimentCache.MINOR_VERSION,
        ExperimentCache.PLATFORM_VERSION);
    assertEquals(1, mFailureCount);
  }

  @Test
  public void testOnlyUpgradesMinorVersion() {
    GoosciExperiment.Experiment proto = createExperimentProto();
    proto.fileVersion.version = 1;
    proto.fileVersion.minorVersion = 0;
    cache.upgradeExperimentVersionIfNeeded(
        proto, new GoosciUserMetadata.ExperimentOverview(), 1, 1, 1);
    assertEquals(1, proto.fileVersion.version);
    assertEquals(1, proto.fileVersion.minorVersion);
  }

  @Test
  public void testUpgradesToMinor2() {
    GoosciExperiment.Experiment proto = createExperimentProto();
    proto.fileVersion.version = 1;
    proto.fileVersion.minorVersion = 1;
    proto.fileVersion.platformVersion = 2;
    proto.fileVersion.platform = GoosciGadgetInfo.GadgetInfo.Platform.ANDROID;
    cache.upgradeExperimentVersionIfNeeded(
        proto, new GoosciUserMetadata.ExperimentOverview(), 1, 2, 500);
    assertEquals(1, proto.fileVersion.version);
    assertEquals(2, proto.fileVersion.minorVersion);
    assertEquals(500, proto.fileVersion.platformVersion);
    assertEquals(GoosciGadgetInfo.GadgetInfo.Platform.ANDROID, proto.fileVersion.platform);
  }

  @Test
  public void testDontDowngradePlatform() {
    GoosciExperiment.Experiment proto = createExperimentProto();
    proto.fileVersion.version = 1;
    proto.fileVersion.minorVersion = 2;
    proto.fileVersion.platformVersion = 1000;
    proto.fileVersion.platform = GoosciGadgetInfo.GadgetInfo.Platform.ANDROID;
    cache.upgradeExperimentVersionIfNeeded(
        proto, new GoosciUserMetadata.ExperimentOverview(), 1, 2, 500);
    assertEquals(1, proto.fileVersion.version);
    assertEquals(2, proto.fileVersion.minorVersion);
    assertEquals(1000, proto.fileVersion.platformVersion);
    assertEquals(GoosciGadgetInfo.GadgetInfo.Platform.ANDROID, proto.fileVersion.platform);
  }

  @Test
  public void testChangePlatformToAndroid() {
    GoosciExperiment.Experiment proto = createExperimentProto();
    proto.fileVersion.version = 1;
    proto.fileVersion.minorVersion = 1;
    proto.fileVersion.platformVersion = 1000;
    proto.fileVersion.platform = GoosciGadgetInfo.GadgetInfo.Platform.IOS;
    cache.upgradeExperimentVersionIfNeeded(
        proto, new GoosciUserMetadata.ExperimentOverview(), 1, 2, 500);
    assertEquals(1, proto.fileVersion.version);
    assertEquals(2, proto.fileVersion.minorVersion);
    assertEquals(500, proto.fileVersion.platformVersion);
    assertEquals(GoosciGadgetInfo.GadgetInfo.Platform.ANDROID, proto.fileVersion.platform);
  }

  @Test
  public void testOnlyUpgradesPlatformVersion() {
    GoosciExperiment.Experiment proto = createExperimentProto();
    proto.fileVersion.version = 1;
    proto.fileVersion.minorVersion = 1;
    proto.fileVersion.platformVersion = 0;
    cache.upgradeExperimentVersionIfNeeded(
        proto, new GoosciUserMetadata.ExperimentOverview(), 1, 1, 1);
    assertEquals(1, proto.fileVersion.version);
    assertEquals(1, proto.fileVersion.minorVersion);
    assertEquals(1, proto.fileVersion.platformVersion);
  }

  @Test
  public void testCantWriteNewerVersion() {
    cache =
        new ExperimentCache(getContext(), appAccount, getFailureExpectedListener(), 0, elm, lsm);
    GoosciExperiment.Experiment proto = createExperimentProto();
    proto.fileVersion.version = ExperimentCache.VERSION;
    proto.fileVersion.minorVersion = ExperimentCache.MINOR_VERSION + 1;
    GoosciUserMetadata.ExperimentOverview overview = new GoosciUserMetadata.ExperimentOverview();
    overview.experimentId = "foo";
    cache.upgradeExperimentVersionIfNeeded(
        proto,
        overview,
        ExperimentCache.VERSION,
        ExperimentCache.MINOR_VERSION,
        ExperimentCache.PLATFORM_VERSION);
    // Version should be unchanged -- don't upgrade minor version.
    assertEquals(ExperimentCache.VERSION, proto.fileVersion.version);
    assertEquals(ExperimentCache.MINOR_VERSION + 1, proto.fileVersion.minorVersion);

    // But no errors yet -- didn't try to save it.
    assertEquals(0, mFailureCount);

    Experiment experiment = Experiment.fromExperiment(proto, overview);
    elm.addExperiment(experiment.getExperimentId());
    lsm.addExperiment(experiment.getExperimentId());
    cache.updateExperiment(experiment, false); // Set this one to active so we can try to write it.
    assertFalse(lsm.getDirty(experiment.getExperimentId()));
    cache.writeActiveExperimentFile();
    assertEquals(1, mFailureCount);
  }

  @Test
  public void testPlatformVersion1To2() {
    // From 1.1.1 to 1.1.2, we index the trials within the experiment.
    GoosciExperiment.Experiment proto = createExperimentProto();
    proto.fileVersion.platformVersion = 1;
    proto.fileVersion.version = 1;
    proto.fileVersion.minorVersion = 1;
    GoosciUserMetadata.ExperimentOverview overview = new GoosciUserMetadata.ExperimentOverview();
    GoosciTrial.Trial trial1 = new GoosciTrial.Trial();
    GoosciTrial.Trial trial2 = new GoosciTrial.Trial();
    proto.trials = new GoosciTrial.Trial[] {trial1, trial2};

    cache.upgradeExperimentVersionIfNeeded(proto, overview, 1, 1, 2);
    assertEquals(0, mFailureCount);
    assertEquals(2, proto.fileVersion.platformVersion);
    assertEquals(2, proto.totalTrials);
    assertEquals(1, proto.trials[0].trialNumberInExperiment);
    assertEquals(2, proto.trials[1].trialNumberInExperiment);
  }

  @NonNull
  private GoosciExperiment.Experiment createExperimentProto() {
    GoosciExperiment.Experiment proto = new GoosciExperiment.Experiment();
    proto.fileVersion = new Version.FileVersion();
    return proto;
  }
}