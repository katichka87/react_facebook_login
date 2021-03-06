/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 */
'use strict';

var React = require('react-native');
var {
  AppRegistry,
  Image,
  ListView,
  StyleSheet,
  Text,
  View,
  DrawerLayoutAndroid,
  ProgressBarAndroid,
  TouchableHighlight,
} = React;

/*
var FBLoginManager = require('NativeModules').FBLoginManager;


var sample = React.createClass({
    getInitialState() {
		var statics = {
            loginText: 'Login with Facebook',
            logoutText: 'Logout from Facebook'
        };
        return {
            statics:statics,
            isLoggedIn: false,
            buttonText: statics.loginText
        };
    },

	componentWillMount: function() {
        var self = this;
        FBLoginManager.getCurrentToken(function(token) {
            if (typeof(token) === 'string' && token.length > 0) {
                self.setState({isLoggedIn:true, buttonText: self.state.statics.logoutText});
            } else {
                self.setState({isLoggedIn:false, buttonText: self.state.statics.loginText});
            }
        })
    },

    _handleEvent(e, data) {
        var result = e || data;
        if(result.type === 'success' && result.profile) {
            try{
                result.profile = JSON.parse(result.profile)
            } catch(err) {
                console.warn('Could not parse facebook profile: ', result.profile);
                console.error(err);
            }
        }
        
        if(result.eventName === 'onLogin' || result.eventName === 'onLoginFound') {
            this.setState({isLoggedIn:true, buttonText: this.state.statics.logoutText});
        } else if(result.eventName === 'onLogout') {
            this.setState({isLoggedIn:false, buttonText: this.state.statics.loginText});
        }
        
        if (result.eventName && this.props.hasOwnProperty(result.eventName)) {
            var event = result.eventName;
            delete result.eventName;
            console.log('Triggering \'%s\' event', event)
            this.props[event](result);
        } else {
            console.log('\'%s\' Event is not defined or recognized', result.eventName)
        }
    },
	
	_onFacebookPress() {
        var permissions = ['email', 'public_profile'];
        if (typeof(this.props.permissions) === 'array'){
            permissions = this.props.permissions;
        }
        
        if(this.state.isLoggedIn){
            FBLoginManager.logout((err,data) => this._handleEvent(err,data));
        } else {
            FBLoginManager.loginWithPermissions(permissions, (err,data) => this._handleEvent(err,data));
        }
    },
	
    render: function() {
        return (
            <TouchableHighlight onPress={this._onFacebookPress}>
                <View style={styles.login}>
                    <Text style={styles.whiteFont}> {this.state.buttonText} </Text>
                </View>
            </TouchableHighlight>
        )
    }
})

var styles = StyleSheet.create({
    login: {
        flex: 1,
        backgroundColor: '#3B5998',
        padding: 10,
        alignItems: 'center'
    },
    whiteFont: {
        color: 'white'
    }
});*/

var {NativeModules} = require('react-native');
var FBLogin = require('./jsmodules/fblogin.js');
var FBLoginManager = NativeModules.FBLoginManager; // if needed

var GoogleLogin = require('./jsmodules/googlelogin.js');
var TwitterLogin = require('./jsmodules/twitterlogin.js');


var sample = React.createClass({
    render: function() {
        return (
		<View>
            <FBLogin
                onLogin={function(e){console.log(e)}}
                onLogout={function(e){console.log(e)}}
                onCancel={function(e){console.log(e)}}
                onPermissionsMissing={function(e){console.log(e)}}
            />
            <GoogleLogin
                onLogin={function(e){console.log(e)}}
                onLogout={function(e){console.log(e)}}
                onCancel={function(e){console.log(e)}}
                onPermissionsMissing={function(e){console.log(e)}}
            />
            <TwitterLogin
                onLogin={function(e){console.log(e)}}
                onLogout={function(e){console.log(e)}}
                onCancel={function(e){console.log(e)}}
                onPermissionsMissing={function(e){console.log(e)}}
            />
		</View>
        );
    },
  
    
});

AppRegistry.registerComponent('sample', () => sample);
