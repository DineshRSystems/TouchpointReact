/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React from 'react';
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  StatusBar,
  NativeModules,
  Button,
    NativeEventEmitter,
} from 'react-native';

import {
  Header,
  LearnMoreLinks,
  Colors,
  DebugInstructions,
  ReloadInstructions,
} from 'react-native/Libraries/NewAppScreen';

const { TouchPointKitBridge } = NativeModules;
const eventEmitter = new NativeEventEmitter(TouchPointKitBridge);

const didActivityCompletedEvent = (event) => {
   console.log(event);
}

const subscription = eventEmitter.addListener('didActivityCompletedEvent', didActivityCompletedEvent);

 NativeModules.TouchPointKitBridge.clearCache();
 NativeModules.TouchPointKitBridge.configure('eliR4075f90VPme4CFAofMTdT3lHiVpObP12IUdp9Vw=', 'y7cRIU1gSAGRB3V9m-_rokGi4pDcnb64yho84mq2U-4=', 0, ['Demo 1', 'Demo 2'], { artist: 'Bruce Springsteen', title: 'Born in the USA' });
 NativeModules.TouchPointKitBridge.enableDebugLogs(true);
 NativeModules.TouchPointKitBridge.shouldApplyAPIFilter(false);
NativeModules.TouchPointKitBridge.setScreen('Demo 2', true);

const App: () => React$Node = () => {
  openActivity = () => {
      NativeModules.TouchPointKitBridge.openActivity('Demo 1');
    }
  return (
    <>
      <StatusBar barStyle="dark-content" />
      <SafeAreaView>
        <ScrollView
          contentInsetAdjustmentBehavior="automatic"
          style={styles.scrollView}>
          <Header />
          {global.HermesInternal == null ? null : (
            <View style={styles.engine}>
              <Text style={styles.footer}>Engine: Hermes</Text>
            </View>
          )}
          <View style={styles.body}>
            <View style={styles.sectionContainer}>
              <Text style={styles.sectionTitle}>Step One</Text>
              <Text style={styles.sectionDescription}>
                Edit <Text style={styles.highlight}>App.js</Text> to change this
                screen and then come back to see your edits.
              </Text>
              <Button
                  onPress={this.openActivity}
                 title="Open Activity"
                 color="#FF6347" />
            </View>
            <View style={styles.sectionContainer}>
              <Text style={styles.sectionTitle}>See Your Changes</Text>
              <Text style={styles.sectionDescription}>
                <ReloadInstructions />
              </Text>
            </View>
            <View style={styles.sectionContainer}>
              <Text style={styles.sectionTitle}>Debug</Text>
              <Text style={styles.sectionDescription}>
                <DebugInstructions />
              </Text>
            </View>
            <View style={styles.sectionContainer}>
              <Text style={styles.sectionTitle}>Learn More</Text>
              <Text style={styles.sectionDescription}>
                Read the docs to discover what to do next:
              </Text>
            </View>
            <LearnMoreLinks />
          </View>
        </ScrollView>
      </SafeAreaView>
    </>
  );
};

const styles = StyleSheet.create({
  scrollView: {
    backgroundColor: Colors.lighter,
  },
  engine: {
    position: 'absolute',
    right: 0,
  },
  body: {
    backgroundColor: Colors.white,
  },
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
    color: Colors.black,
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
    color: Colors.dark,
  },
  highlight: {
    fontWeight: '700',
  },
  footer: {
    color: Colors.dark,
    fontSize: 12,
    fontWeight: '600',
    padding: 4,
    paddingRight: 12,
    textAlign: 'right',
  },
});

export default App;
