var React = require('react-native');

var {
    NativeModules: {
      GoogleSignin
    },
    StyleSheet,
    View,
    TouchableHighlight,
    Text,
    DeviceEventEmitter
} = React;


var GoogleLogin = React.createClass({
	
	getInitialState() {
        var statics = {
            loginText: 'Login with Google',
            logoutText: 'Logout from Google'
        };
        return {
            statics:statics,
            isLoggedIn: false,
            buttonText: statics.loginText
        };
    },
	
	componentDidMount: function() {
        GoogleSignin.init();
		// Callback on sign-in errors
        DeviceEventEmitter.addListener('googleSignInError', (error) => {
            console.log('ERROR signin in', error);
			this.setState({isLoggedIn:false, buttonText: this.state.statics.loginText});
        });

        // callback on sign-in success
        DeviceEventEmitter.addListener('googleSignIn', (user) => {
            console.log(user);
            /*this.setState({user: user});*/
			this.setState({isLoggedIn:true, buttonText: this.state.statics.logoutText});
        });
		
		// callback on sign-out success
        DeviceEventEmitter.addListener('googleSignOut', (user) => {
            console.log(user);
			this.setState({isLoggedIn:false, buttonText: this.state.statics.loginText});
        });
    },
	
	_onGooglePress() {
		
		if(this.state.isLoggedIn) {
            GoogleSignin.signOut();
        } else {
            GoogleSignin.signIn();
        }
	},
	
	render: function() {
        return (
          <TouchableHighlight onPress = {this._onGooglePress}>
              <View style={styles.login}>
                  <Text style={styles.whiteFont}> {this.state.buttonText} </Text>
              </View>
          </TouchableHighlight>
        )
    }
	
	
});

var styles = StyleSheet.create({
    login: {
        flex: 1,
        backgroundColor: '#D64A38',
        padding: 10,
        alignItems: 'center'
    },
    whiteFont: {
        color: 'white'
    }
});

module.exports = GoogleLogin;
