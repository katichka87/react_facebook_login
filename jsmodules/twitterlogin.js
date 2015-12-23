var React = require('react-native');
var {
    NativeModules,
    StyleSheet,
    View,
    TouchableHighlight,
    Text
} = React;

var TwitterLoginManager = NativeModules.TwitterLoginManager;

var itypeof = function (val) {
    return Object.prototype.toString.call(val).replace(/(\[|object|\s|\])/g, '').toLowerCase();
};


var TwitterLogin = React.createClass({
    getInitialState() {
        var statics = {
           loginText: 'Login with Twitter',
           logoutText: 'Logout from Twitter'
        };
        return {
            statics:statics,
            isLoggedIn: false,
            buttonText: statics.loginText
        };
    },
	
    componentWillMount: function(){
		
        var self = this;
        TwitterLoginManager.getCurrentToken(function(token) {
            if(itypeof(token) === 'string' && token.length > 0){
            self.setState({isLoggedIn:true, buttonText: self.state.statics.logoutText});
        } else {
          self.setState({isLoggedIn:false, buttonText: self.state.statics.loginText});
        }})
    },

    _handleEvent(act, token) {
        
        if (act === 'onLogin' && token !== null) {
            this.setState({isLoggedIn:true, buttonText: this.state.statics.logoutText});
        } else if (act === 'onLogout') {
            this.setState({isLoggedIn:false, buttonText: this.state.statics.loginText});
        }
    },

    _onTwitterPress() {
    
        if(this.state.isLoggedIn){
          TwitterLoginManager.logout();
		  this._handleEvent("onLogout", null)
        }else{
          TwitterLoginManager.login((token) => this._handleEvent("onLogin", token));
        }
    },

    render: function(){
        return (
            <TouchableHighlight onPress={this._onTwitterPress}>
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
        backgroundColor: '#00B6F1',
        padding: 10,
        alignItems: 'center'
    },
    whiteFont: {
        color: 'white'
    }
});

module.exports = TwitterLogin;
