/**
 * 版本修订记录
 *
 * --- 2.0.1 霍彦会 2020年6月8日，增加国办签章公安数据接口（esp开头的6个接口）,增加createP11ObjectX函数，方便P11ObjectX的使用。
 * --- 2.0.0 霍彦会 2020年5月8日，建立版本。
 *
 */

var HebcaClient = HebcaClient || {};
(function ($c) {

	//JS库版本相关信息
	var VersionInfo = {
		version: "2.0.1",
		versionInt: 0x020001,
		productName: "HebcaP11Object",
		productNameCn: "河北CA跨浏览器证书中间件",
		getInfo: function () {
			return this.productName + " V"+this.version;
		}
	};

	//错误描述定义的变量定义
    var EnvVar={
        CLIENT_NAME: { name: "CLIENT_NAME", value: "河北CA数字证书助手一证通用版" },
        WEB_HOST: { name: "WEB_HOST", value: "www.hebca.com"},
        CLIENT_DOWNLOAD_URL: {name: "CLIENT_DOWNLOAD_URL", value: "http://helper.pasiatec.com/client/HEBCA-helper.exe"}
    };

    //JS库使用的错误定义
	var ErrorMapping={
		ERR_NO_SELECT_CERT: { err: 1000,  message: "未选择证书" },
		ERR_NO_SELECT_SIGN_CERT: { err: 1001,  message: "未选择签名证书" },
		ERR_NO_SELECT_CRYPT_CERT: { err: 1002,  message: "未选择加密证书" },
		ERR_CREATE_P11_IE: { err: 1003, message: "没有安装河北CA证书助手客户端软件或IE阻止其运行" },
		ERR_OPEN_P11_SERVICE: { err: 1004, message: '打开河北CA证书服务失败'},
		ERR_NOT_FIND_CERT: { err: 1005, message: '没有找到对应的证书' },
		ERR_NOT_CRYPT_CERT: { err: 1006, message: '请添加接收者证书' },
		ERR_NOT_SUPPORT_FUN: { err: 1007, message: '不支持此功能' },
		ERR_NOT_CERT_SELECTOR: { err: 1008, message: '没有指定选择项' },
		ERR_WEBSOCKET_NO_RESPONES: { err: 1009, message: 'P11服务没有回应' }
	};

	//自定义错误描述，适合hebca，以文本方式显示
	function errorStyle_hebca_text(client) {
		//没有安装河北CA证书助手客户端软件或IE阻止其运行
		client.defineError(1003,"$ORG_MESSAGE，请确认：\n1.检查您的电脑中是否安装了$CLIENT_NAME，如没有安装，请到河北CA网站$WEB_HOST下载安装；\n2.如已安装助手，请打开助手执行故障检测，然后重新浏览器后重试.");
		//打开河北CA证书服务失败,crhome
		client.defineError(1004,"$ORG_MESSAGE\n1.如果您还没有安装$CLIENT_NAME，请到河北CA网站$WEB_HOST下载安装；\n 2.如已安装助手，请在弹出的对话框中，点击“打开或者允许”，然后刷新页面重试.");

		//数字证书不存在
		client.defineError(-536145917, "$ORG_MESSAGE，请确认您已经将河北CA数字证书USB Key插入电脑中，或者将数字证书USB Key拔插后重试");

		//程序执行失败， 请拔插key后重试
		client.defineError(-536145887, "$ORG_MESSAGE，请将数字证书USB Key拔插后重试");

		client.defineError("other","$ORG_MESSAGE，请联系河北CA客服，客服电话：400-707-3355");
	}

	function errorStyle_hebca_html(client) {
		//没有安装河北CA证书助手客户端软件或IE阻止其运行
		client.defineError(1003,"$ORG_MESSAGE，请确认：<br /> 1.检查您的电脑中是否安装$CLIENT_NAME，如没有安装，请<a herf='$CLIENT_DOWNLOAD_URL'>点击这里</a>下载安装；<br />2.如已安装助手，请打开助手执行故障检测，然后重新浏览器后重试.");
		//打开河北CA证书服务失败,crhome
		client.defineError(1004,"$ORG_MESSAGE<br />1.如果您还没有安装$CLIENT_NAME，请<a href='$CLIENT_DOWNLOAD_URL' target='_blank'>点击这里</a>下载安装； <br /> 2.如已安装助手，请在弹出的对话框中，点击“打开或者允许”，然后刷新页面重试.");

		//数字证书不存在
		client.defineError(-536145917, "$ORG_MESSAGE，请确认您已经将河北CA数字证书USB Key插入电脑中，或者将数字证书USB Key拔插后重试");

		//程序执行失败， 请拔插key后重试
		client.defineError(-536145887, "$ORG_MESSAGE，请将数字证书USB Key拔插后重试");

		client.defineError("other","$ORG_MESSAGE(错误号:$ERR)，请联系河北CA客服，客服电话：400-707-3355, 或者<a href='http://v1.live800.com/live800/chatClient/chatbox.jsp?companyID=551849&configID=42419&jid=7440845194&s=1' target='_blank'>点击这里</a>联系在线客服");
	}

	/**
	 * HebcaP11Object
	 * @constructor
	 * @param param 初始化参数，可以接收初始化或者后续使用的错误事件，可选。如果缺省此函数，在初始化失败时，直接抛出异常。
	 *              如果提供了此函数，并包含相应的回调函数，在初始化失败时，进行回调。
	 * {
	 * 	   debug: true|false, default false
	 * 	   errorStyle: "hebca/text | hebca/html | default"
	 * 	   errorDivId:
	 * 	   defaultErrorShow:
	 *     success: function(){}
	 *     fail: function(e){}  在无法初始化Hebca控件或者服务时将回调此函数
	 * }
	 */
	function HebcaP11Object(param){
		this.initParam= param || {};

		this.isIE = window.ActiveXObject !== undefined;
		var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
		this.isEdge = userAgent.indexOf("Edge") > -1; //判断是否IE的Edge浏览器

		//IE
		this.certMgr = null;
		this.cert=null;
		this.signCert=null;
		this.cryptCert=null;
		this.device=null;
		this.util=null;

		//非IE浏览器下
		this.socket = null;
		this.host = "ws://localhost:17212/test";

		this.redefineErrorMapping={};
		if(this.initParam){
			this._setErrorStyle(this.initParam.errorStyle);
		}

		this.debug("P11 Object begin init ( isIE = "+this.isIE+", isEdge = "+ this.isEdge+" )");
		this._initP11Object();
	}

	HebcaP11Object.prototype._setErrorStyle=function(style){
		style=style||"default";
		if(style==="hebca/text"){
			errorStyle_hebca_text(this);
		}
		else if(style==="hebca/html"){
			errorStyle_hebca_html(this);
		}
		else{
			//default
			//do nothing
		}
	};

	HebcaP11Object.prototype._runEnvVar=function(message, envVar){
		return message.replace("$"+envVar.name, envVar.value);
	};

	HebcaP11Object.prototype.defineError=function(errNum,message){
		message=this._runEnvVar(message,EnvVar.CLIENT_NAME);
		message=this._runEnvVar(message,EnvVar.CLIENT_DOWNLOAD_URL);
		message=this._runEnvVar(message,EnvVar.WEB_HOST);

		if(typeof errNum === "number"){
			this.redefineErrorMapping[errNum.toString()]=message;
		}
		else {
			this.redefineErrorMapping[errNum]=message;
		}
	};

	HebcaP11Object.prototype._getErrorMessage=function(errNum,origMessage){
		if(errNum===undefined || errNum===null ){
			return origMessage
		}

		var redefine = this.redefineErrorMapping[errNum.toString()];
		if(redefine===undefined){
			var other=this.redefineErrorMapping["other"];
			if(other!==undefined){
				other = other.replace("$ORG_MESSAGE",origMessage);
				other = other.replace("$ERR", errNum.toString());
				return other;
			}
			else{
				return origMessage;
			}
		}
		else{
			redefine = redefine.replace("$ORG_MESSAGE",origMessage);
			redefine = redefine.replace("$ERR", errNum.toString());
			return redefine;
		}
	};

	HebcaP11Object.prototype._createError=function(err){
		var e = new Error(this._getErrorMessage(err.err,err.message));
		e.err = err.err;
		e.mapped=true;
		return e;
	};

	HebcaP11Object.prototype._createErrorP11ObjectIE=function(e){
		var error = new Error(this._getErrorMessage(e.number,e.message));
		error.err = e.number;
		e.mapped=true;
		return error;
	};

	HebcaP11Object.prototype._createErrorP11Service=function(ret,message){
		var e = new Error(this._getErrorMessage(ret,message));
		e.err = ret;
		e.mapped=true;
		return e;
	};

	HebcaP11Object.prototype._getCert=function(){
		if(this.cert==null){
			throw this._createError(ErrorMapping.ERR_NO_SELECT_CERT);
		}
		return this.cert;
	};

	HebcaP11Object.prototype._getSignCert=function(){
		if(this.signCert==null){
			throw this._createError(ErrorMapping.ERR_NO_SELECT_SIGN_CERT);
		}
		return this.signCert;
	};

	HebcaP11Object.prototype._getCryptCert=function(){
		if(this.cryptCert==null){
			throw this._createError(ErrorMapping.ERR_NO_SELECT_CRYPT_CERT);
		}
		return this.cryptCert;
	};

	HebcaP11Object.prototype._showError=function(e,param){
		if(param && param.ignoreError){
			return;  //忽略错误，不显示
		}

		if(this.initParam && this.initParam.errorDivId){
			//在应用的div中显示错误信息
			var div=document.getElementById(this.initParam.errorDivId);
			if(div){
				div.innerHTML=e.message;
			}
		}
		else{
			if(this.initParam && (this.initParam.defaultErrorShow===undefined || this.initParam.defineErrorShow===true) ){
				this._showErrorInDefaultPane(e);
			}
		}
	};

	HebcaP11Object.prototype._showErrorInDefaultPane=function(e){
		var hebca_errorPaneContainer=document.getElementById("hebca_errorPaneContainer");
		if(!hebca_errorPaneContainer)
		{
			var cssContainer="position:fixed; top:1px; padding: 10px; background-color: lightpink; width: 100%; text-align: left; visibility: hidden";
			var cssClose1="position:fixed; top:15px; right: 10px; display: inline-block; width: 15px; height: 4px; background: #CCCCCC; transform: rotate(45deg);";
			var cssClose2="position:fixed; top:15px; right: 10px; display: block; width: 15px; height: 4px; background: #CCCCCC; transform: rotate(-45deg);";
			var onclick="javascript: document.getElementById('hebca_errorPaneContainer').style.visibility='hidden';";

			if(navigator && navigator.userAgent.match(/msie/i)){
				//ie
				var errorPaneContainer = document.createElement("div");
				errorPaneContainer.setAttribute("id", "hebca_errorPaneContainer");
				errorPaneContainer.setAttribute("style", cssContainer);
				errorPaneContainer.style.backgroundColor = "lightpink";
				errorPaneContainer.style.padding = "10px";
				errorPaneContainer.style.width = "100%";
				errorPaneContainer.style.textAlign="left";
				errorPaneContainer.style.visibility="hidden";

				var errorPane = document.createElement('span');
				errorPane.setAttribute('id','hebca_errorPane');
				errorPaneContainer.appendChild(errorPane);

				var close = document.createElement('span');
				close.setAttribute('id','close');
				close.onclick=function(){
					document.getElementById('hebca_errorPaneContainer').style.visibility='hidden';
				};
				close.innerText="点击关闭提示框";
				close.style.textAlign="left";
				//close.style.position = "fixed";
				//close.style.top = "15px";
				close.style.paddingLeft = "30px";
				close.style.textDecoration="underline";
				errorPaneContainer.appendChild(close);

				document.body.insertBefore(errorPaneContainer, document.body.children[0]);
			}
			else
			{
				var pane='<div id="hebca_errorPane"></div><span id="close1" style="'+cssClose1+'" onclick="'+onclick+'"></span><span id="close2" style="'+cssClose2+'" onclick="'+onclick+'"></span>';
				var errorPane = document.createElement("div");
				errorPane.setAttribute("id", "hebca_errorPaneContainer");
				errorPane.setAttribute("style", cssContainer);
				errorPane.innerHTML=pane;
				document.body.appendChild(errorPane);
			}

			hebca_errorPaneContainer=document.getElementById("hebca_errorPaneContainer");
		}

		hebca_errorPaneContainer.style.backgroundColor="lightcoral";
		hebca_errorPaneContainer.style.visibility="visible";

		if(navigator && navigator.userAgent.match(/msie/i)){
			var p=document.getElementById("hebca_errorPane");
			p.innerHTML=e.message;
		}
		else{
			var p=document.getElementById("hebca_errorPane");
			p.innerHTML=e.message;
		}

	};

	/**
	 * 设置调试
	 * @param d true}false  打开关闭调试
	 */
	HebcaP11Object.prototype.setDebug=function(d){
		if(!this.initParam){
			this.initParam={};
		}
		this.initParam.debug=d;
	};

	/**
	 * 输出日志
	 */
	HebcaP11Object.prototype.debug=function(){
		if(this.initParam && this.initParam.debug){
			var arg=["[HebcaP11Object]:"];
			for(var i=0;i<arguments.length;i++){
				arg.push(arguments[i]);
			}
			try
			{
				console.log.apply(this, arg);
			}
			catch (e) {
				//do nothing
			}
		}
	};

	HebcaP11Object.prototype._debugFunBegin=function(name,param){
		this.debug(name+" begin param: ",param);
	};

	HebcaP11Object.prototype._createP11ObjectIE=function(){
		if (null != this.certMgr){
			return this.certMgr;
		}

		if (this.isIE){
			// IE 浏览器创建插件
			try{
				this.certMgr=new ActiveXObject("HebcaP11X.CertMgr");
				this.certMgr.Licence = "amViY56Xmp2cnpeanZyel5qdnJ6Xmp2cYWhlYoQsftgudcLw21NcfvO8eN13ICbS";
				this.util=this.certMgr.Util;

				this.debug("Create HebcaP11X.CertMgr Object Success");
			}
			catch(e){
				this.debug("Create HebcaP11X.CertMgr Object Failed: "+e.message);
				var ex=this._createError(ErrorMapping.ERR_CREATE_P11_IE);
				this._showError(ex,param);
				if(this.initParam && this.initParam.fail){
					this.initParam.fail(ex);
				}
				else{
					//如果没有定义回调函数接收错误，则直接抛出异常
					throw ex;
				}
			}
		}
	};

	HebcaP11Object.prototype._connectP11WebSocketService=function (successCallback) {
		try{
			var that=this;
			this.socket = new WebSocket(this.host);
			this.debug("WebSocket begin connect...");
			this.socket.onopen = function(){
				that.debug("WebSocket onopen success");
				that.socket.send('0|'+  new Date().getTime());
			};
			this.socket.onclose = function(){
				that.debug("WebSocket onclose");
				/*var ex=that._createError(ErrorMapping.ERR_OPEN_P11_SERVICE);
				that._showError(ex);
				if(that.initParam && that.initParam.fail){
					that.initParam.fail(ex);
				}*/
				//document.location.href="P11://";
				that.socket=null;
			};
			this.socket.onerror = function(e){
				that.debug("WebSocket onerror");
				var ex=that._createError(ErrorMapping.ERR_OPEN_P11_SERVICE);
				that._showError(ex);
				if(that.initParam && that.initParam.fail){
					that.initParam.fail(ex);
				}
				document.location.href="P11://";
				that.socket=null;
			};
			this.socket.onmessage = function(msg){
				that.debug("WebSocket onmessage success");
				var args  = msg.data.split('|');
				if(args[0] === "0" && args[1].indexOf("Welcome")!== -1){
					that.debug("Webscoket Welcome");
					if(that.initParam && that.initParam.success){
						that.initParam.success(that);
					}

					if(successCallback){
						successCallback();
					}
				}
			}
		} catch(e){
			this.debug("WebSocket excpetion:"+e.message);
			document.location.href="P11://";

			var ex=this._createError(ErrorMapping.ERR_OPEN_P11_SERVICE);
			this._showError(ex);
			if(this.initParam && this.initParam.fail){
				this.initParam.fail(ex);
			}
			else{
				//如果没有定义回调函数接收错误，则直接抛出异常
				throw ex;
			}
		}
	};

	HebcaP11Object.prototype._getDebugFlagFromLocal=function(next){
		var that=this;
		if(!this.initParam || this.initParam.debug==false){
			//在没有打开日志时，检测注册表是否打开了日志
			this.readReg({
				key: "\HKEY_CURRENT_USER\Software\Hebca\ClientX",
				name: 'p11ObjectDebug',
				ignoreError: true,  //一定要忽略错误，如果读注册表识别，则忽略此错误。
				success: function (v) {
					if( v === 1 || v==='debug' || v==='Debug'){
						that.setDebug(true);
						this.debug("debug enabled by local reg HKEY_CURRENT_USER\\Software\\Hebca\\ClientX\\p11ObjectDebug");
					}

					next();
				},
				fail: function (e) {
					//do nothing
					next();
				}
			});
		}
		else{
			next();
		}
	};

	HebcaP11Object.prototype._initP11Object=function(){
		var that=this;
		if(this.isIE){
			this._createP11ObjectIE();

			this._getDebugFlagFromLocal(function () {
				if(that.initParam && that.initParam.success){
					that.initParam.success(that);
				}
			});
		}else {
			this._connectP11WebSocketService(function () {
				that._getDebugFlagFromLocal(function () {
					//do nothing
				});
			});
		}
	};


	HebcaP11Object.prototype._webSocketSend=function(fun,param,success,fail){
		if(this.socket===null){
			var that=this;
			this._connectP11WebSocketService(function () {
				that._webSocketSendImp(fun,param,success,fail);
			});
		}
		else{
			this._webSocketSendImp(fun,param,success,fail);
		}
	};

	HebcaP11Object.prototype._webSocketSendImp=function(fun,param,success,fail){
		var msgSend ='1|'+fun;
		if(param){
			msgSend+="|"+param;
		}

		this.debug("WebSocket Send: "+msgSend);
		this.socket.send(msgSend);
		var that=this;

		this.socket.onmessage = function(msg){

			that.debug("WebSocket Recieve: "+msg.data);
			var args  = msg.data.split('|');
			if(args[1] == fun){
				var json = eval("("+args[2]+")");
				if(json.ret == 0){
					if(success){
						success(json);
					}
				}else{
					if(fail){
						fail(that._createErrorP11Service(json.ret, json.msg));
					}
				}
			}
		}
	};

	HebcaP11Object.prototype._onSuccess=function(name,param,value){
		if(value===undefined){
			this.debug(name + " end success ");
		}
		else{
			this.debug(name + " end success :",value);
		}

		if(param && param.success){
			param.success(value);
		}
	};

	HebcaP11Object.prototype._onFail=function(name,param,e){
		var errNum=0;

		if(typeof e.err!=="undefined"){
			errNum=e.err
		}
		else if(typeof e.number!=="undefined"){
			errNum=e.number;
		}
		this.debug(name + " failed: ",e, "err:", errNum );

		if(e.mapped)
		{
			//do nothing
		}
		else{
			var message = this._getErrorMessage(errNum, e.message);
			e=new Error(message);
			e.err=errNum;
			e.mapped=true;
		}
		this._showError(e,param);

		if(param && param.fail){
			param.fail(e);
		}
	};

	/**
	 * 获取设备个数
	 * @param param
	 * {
	 *     success：function(value){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.getDeviceCount=function (param) {
		var name="getDeviceCount";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var count = this.certMgr.GetDeviceCount();
				this._onSuccess(name,param,count);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('GetDeviceCount',null,function (json) {
				that._onSuccess(name,param,json.count);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 获取证书个数
	 * @param param
	 * {
	 *     success：function(value){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.getCertCount=function (param) {
		var name="getCertCount";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var count = this.certMgr.GetCertCount();
				this._onSuccess(name,param,count);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('GetCertCount',null,function (json) {
				that._onSuccess(name,param,json.count);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 获取签名证书个数
	 * @param param
	 * {
	 *     success：function(count){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.getSignCertCount=function(param){
		var name="getSignCertCount";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var count = this.certMgr.GetSignCertCount();
				this._onSuccess(name,param,count);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('GetSignCertCount',null,function (json) {
				that._onSuccess(name,param,json.count);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 选择签名证书
	 * @param param
	 * {
	 *     success：function(){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.selectSignCert=function (param) {
		var name="getSignCertCount";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try {
				this.cert = this.signCert = this.certMgr.SelectSignCert();
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('SelectSignCert',null,function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 选择加密证书
	 * @param param
	 * {
	 *     success：function(){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.selectCryptCert=function (param) {
		var name="selectCryptCert";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try {
				this.cert = this.cryptCert = this.certMgr.SelectEncryptCert();
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('SelectCryptCert',null,function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 获取签名证书
	 * @param param
	 * {
	 * 		index: xx, 索引
	 *     success：function(){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.getSignCert=function(param){
		var name="getSignCert";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try {
				this.cert = this.signCert = this.certMgr.GetSignCert(param.index);
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('GetSignCert','{"index":'+ param.index +'}',function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 根据证书主题的某项，选择证书。 注意：如果有多个证书的该项值相同，则选择最后一个。所以最好通过唯一性项选择，比如：G、身份证、cn
	 * @param param
	 * {
	 *     index: xx,   //SubjectItem 索引
	 *     value: xx    //值
	 * }
	 */
	HebcaP11Object.prototype.selectSignCertBySubjectItem=function (param) {
		var name="selectSignCertBySubjectItem";
		this._debugFunBegin(name,param);
		var choose=-1;
		var that=this;
		this.forEachSignCert({
			fun: function (index,resolve,reject) {
				that.getSubjectItem({
					index: param.index,
					success: function (v) {
						if(v===param.value)
						{
							choose=index;
						}
						resolve();
					},
					fail: function (e) {
						reject(e);
					}
				});
			},
			success: function () {
				if(choose==-1){
					that._onFail(name,param,that._createError(ErrorMapping.ERR_NOT_FIND_CERT));
				}
				else{
					that.getSignCert({
						index: choose,
						success: function () {
							that._onSuccess(name,param,choose);
						},
						fail: function (e) {
							that._onFail(name,param,e);
						}
					});
				}
			},
			fail: function (e) {
				that._onFail(name,param,e);
			}
		});
	};

	/**
	 * 选择签名证书，可以过滤设备
	 * @param param
	 * {
	 *     deviceNames: Array,
	 *     success：function(){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.selectSignCertFilterDevice=function(param){
		var name="selectSignCertFilterDevice";
		this._debugFunBegin(name,param);
		if(this.isIE){
			for(var i=0;i<param.deviceNames.length;i++){
				if(param.deviceNames[i]!==""){
					this.certMgr.AddIgnoreDevice(param.deviceNames[i]);
				}
			}

			var that=this;
			var deleteDevicesFun=function(){
				for(var i=0;i<param.deviceNames.length;i++){
					if(param.deviceNames[i]!==""){
						that.certMgr.DeleteIgnoreDevice(param.deviceNames[i]);
					}
				}
			};

			try {
				this.cert = this.signCert = this.certMgr.SelectSignCert();
				deleteDevicesFun();
				this._onSuccess(name,param);
			}catch(e){
				deleteDevicesFun();
				this._onFail(name,param,e);
			}
		}else{
			var deviceNames="";
			for(var i=0;i<param.deviceNames.length;i++){
				if(param.deviceNames[i]!==""){
					deviceNames+=param.deviceNames[i];
					if(i!=param.deviceNames.length-1){
						deviceNames+=";";
					}
				}
			}

			var that=this;
			this._webSocketSend('SelectSignCertFilterDevice','{"deviceNames":"'+ deviceNames + '"}',function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});

		}
	};

	/**
	 * 组合选择证书
	 * @param param
	 * {
	 *      g
			cn
			subjectIndex
			subjectValue
			index
			sn
	 * }
	 */
	HebcaP11Object.prototype.selectSignCertCombine=function(param){
		var name="selectSignCertCombine";
		this._debugFunBegin(name,param);
		var that=this;
		if(param.index!=undefined && param.index!=null ){
			this.getSignCert({
				index: param.index,
				success: function () {
					that._onSuccess(name,param);
				},
				fail: function (e) {
					that._onFail(name,param,e);
				}
			})
		}
		else if(param.cn && param.cn!=="" ){
			this.selectSignCertBySubjectItem({
				index: 0,
				value: param.cn,
				success: function () {
					that._onSuccess(name,param);
				},
				fail: function (e) {
					that._onFail(name,param,e);
				}
			});
		}
		else if(param.g && param.g!=="" ){
			this.selectSignCertBySubjectItem({
				index: 7,
				value: param.g,
				success: function () {
					that._onSuccess(name,param);
				},
				fail: function (e) {
					that._onFail(name,param,e);
				}
			});
		}
		else if(param.subjectIndex!=undefined && param.subjectIndex!=null ){
			this.selectSignCertBySubjectItem({
				index: param.subjectIndex,
				value: param.subjectValue,
				success: function () {
					that._onSuccess(name,param);
				},
				fail: function (e) {
					that._onFail(name,param,e);
				}
			});
		}
		else if(param.sn && param.sn!=="" ){
			this.getCertList({
				success: function (r) {
					var snList = r.certSnList.split(";");
					var selectIndex=-1;
					for(var i=0;i<snList.length;i++){
						if(snList[i]===param.sn){
							selectIndex=i;
							break;
						}
					}

					if(selectIndex!=-1){
						that.getSignCert({
							index: i,
							success: function () {
								that._onSuccess(name,param);
							},
							fail: function (e) {
								that._onFail(name,param,e);
							}
						});
					}
					else{
						that._onFail(name,param,that._createError(ErrorMapping.ERR_NOT_FIND_CERT));
					}
				},
				fail: function (e) {
					that._onFail(name,param,e);
				}
			})
		}
		else{
			this._onFail(name,param,this._createError(ErrorMapping.ERR_NOT_CERT_SELECTOR));
		}
	};

	/**
	 * 获取主题项
	 * @param param
	 * {
	 *     index: 主题项名称或者索引
	 *     success：function(value){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.getSubjectItem=function(param){
		var name="getSubjectItem";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var content = this._getCert().GetSubjectItem(param.index);
				this._onSuccess(name,param,content);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('GetSubjectItem','{"index":'+ param.index +'}',function (json) {
				that._onSuccess(name,param,json.content);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 获取签名证书有效期
	 * @param param
	 * {
	 * 		index: xx, 索引
	 *     success：function(notafter){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.getSignCertNotAfter=function(param){
		var name="getSignCertNotAfter";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var signCert = this.certMgr.GetSignCert(param.index);
				var dateNotAfter =  signCert.NotAfter;
				this._onSuccess(name,param,dateNotAfter);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('GetSignCertNotAfter','{"index":'+ param.index +'}',function (json) {
				that._onSuccess(name,param,json.certnotafter);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 获取证书字符串
	 * @param param
	 * {
	 *     success：function(value){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.getCertB64=function(param) {
		var name="getCertB64";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var content = this._getCert().GetCertB64();
				this._onSuccess(name,param,content);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else {
			var that=this;
			this._webSocketSend('GetCertB64',null,function (json) {
				that._onSuccess(name,param,json.cert);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 指定的证书是否在线
	 * @param param
	 * {
	 *     cn：'',
	 *     success：function(result){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.isCertOnline=function(param){
		var name="isCertOnline";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var count = this.certMgr.GetCertCount();
				var online = 0;
				for(var i=0;i<count;i++)
				{
					var c = this.certMgr.GetCert(i);
					var tempCN = c.GetSubjectItem(0);
					if( param.cn === tempCN )
					{
						online = 1;
						break;
					}
				}
				this._onSuccess(name,param,online);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('IsCertOnline','{"cn":"'+ param.cn + '"}',function (json) {
				that._onSuccess(name,param,json.result);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 带界面登录
	 * @param param
	 * {
	 *     success：function(){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.uiLogin=function(param){
		var name="uiLogin";
		this._debugFunBegin(name,param);
		var that=this;
		if(this.isIE){
			try{
				this._getCert().UILogin();
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			this._webSocketSend('UILogin',null,function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 登录
	 * @param param
	 * {
	 *     pin：'', 密码
	 *     success：function(){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.login=function(param){
		var name="login";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				this._getSignCert().Login(param.pin);
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('Login','{"pin":"'+ param.pin +'"}',function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * CSP登录
	 * @param param
	 * {
	 *     pin：'', 密码
	 *     success：function(){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.cspLogin=function(param){
		var name="cspLogin";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				this._getSignCert().CspLogin(param.pin);
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('CspLogin','{"pin":"'+ param.pin +'"}',function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 *
	 * @param param
	 * {
	 *     certSN, pin,
	 * }
	 */
	HebcaP11Object.prototype.unUICertLogin=function(param){
		var name="unUICertLogin";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var selectCert=null;
				var count=this.certMgr.GetSignCertCount();
				for(var i=0;i<count;i++){
					var c=this.certMgr.GetSignCert(i);
					if(param.certSN === c.GetSerialNumberHex()){
						selectCert=c;
						break;
					}
				}

				if(selectCert!=null){
					selectCert.Login(param.pin);
					this._onSuccess(name,param);
				}
				else{
					this._onFail(name,param,this._createError(ErrorMapping.ERR_NOT_FIND_CERT));
				}
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('UnUICertLogin','{"certSN":"'+ param.certSN + '","password":"'+param.pin+'"}',function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 证书是否已登录
	 * @param param
	 * {
	 *     cn：'',
	 *     success：function(result){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.isCertLogined=function(param){
		var name="isCertLogined";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var count = this.certMgr.GetCertCount();
				var online = 0;
				for(var i=0;i<count;i++)
				{
					var c = this.certMgr.GetCert(i);
					var tempCN = c.GetSubjectItem(0);
					if( param.cn === tempCN )
					{
						online = c.Logined;
						if( online )
							break;
					}
				}
				this._onSuccess(name,param,online);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('IsCertLogined','{"cn":"'+ param.cn + '"}',function (json) {
				that._onSuccess(name,param,json.result);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 登出
	 * @param param
	 * {
	 *     success：function(result){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.logout=function(param){
		var name="logout";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				this._getSignCert().Logout();
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('Logout',null,function (json) {
				that._onSuccess(name,param,json.result);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 签名
	 * @param param
	 * {
	 *     content:"",  签名原文
	 *     success：function(signature){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.sign=function(param){
		var name="sign";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var signature = this._getSignCert().SignText(param.content, 1);
				this._onSuccess(name,param,signature);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('SignText','{"signText":"'+ param.content +'"}',function (json) {
				that._onSuccess(name,param,json.signature);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 签名B64
	 * @param param
	 * {
	 * 		content: "" ,
	 *     success：function(signature){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.signB64=function(param){
		var name="signB64";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var signature = this._getSignCert().SignB64(param.content, 1);
				this._onSuccess(name,param,signature);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('SignB64','{"b64data":"'+ param.content +'"}',function (json) {
				that._onSuccess(name,param,json.signature);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};


	/**
	 *
	 * @param param
	 * {
	 *     certSN, content,
	 * }
	 */
	HebcaP11Object.prototype.unUISign=function(param){
		var name="unUISign";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var selectCert=null;
				var count=this.certMgr.GetSignCertCount();
				for(var i=0;i<count;i++){
					var c=this.certMgr.GetSignCert(i);
					if(param.certSN === c.GetSerialNumberHex()){
						selectCert=c;
						break;
					}
				}

				if(selectCert!=null){
					selectCert.IsSilent=true;
					selectCert.SignB64(param.content,1);
					selectCert.IsSilent=false;
					this._onSuccess(name,param);
				}
				else{
					this._onFail(name,param,this._createError(ErrorMapping.ERR_NOT_FIND_CERT));
				}
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('UnUISign','{"certSN":"'+ param.certSN + '","b64data":"'+param.content+'"}',function (json) {
				that._onSuccess(name,param,json.signature);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};


	/**
	 * 签名，内含选择证书SelectSignCert，以及获取签名证书
	 * @param param
	 * {
	 *     content: '',
	 *     format: 'text'|'b64', 默认text
	 *     pin: 密码
	 *     g:
	 *     cn:
	 *     subjectIndex
	 *     subjectValue
	 *     index
	 *     sn
	 *     success: function(result){}     result: { cert: signCert, signature: signature }
	 *     fail: function(e){}
	 * }
	 */
	HebcaP11Object.prototype.signContent=function(param){
		var name="signContent";
		this._debugFunBegin(name,param);
		var seq=new $c.Seq();
		var that=this;
		seq.then(function () {
			//选择证书
			if( (param.g && param.g!=="") ||
				(param.cn && param.cn!=="") ||
				(param.subjectIndex!=undefined && param.subjectIndex!=null) ||
				(param.index!=undefined && param.index!=null) ||
				(param.sn && param.sn!=="")
			)
			{
				//根据用户指定项选择证书
				var p1={
					g: param.g,
					cn: param.cn,
					subjectIndex: param.subjectIndex,
					subjectValue: param.subjectValue,
					index: param.index,
					sn: param.sn,
					success: function () {
						seq.resolve();
					},
					fail:function (e) {
						seq.reject(e);
					}
				};
				that.selectSignCertCombine(p1);
			}
			else{
				//弹框选择
				that.selectSignCert({
					success: function () {
						seq.resolve();
					},
					fail:function (e) {
						seq.reject(e);
					}
				});
			}
		})
			.then(function () {
				//如果指定了pin，则登录
				if(param.pin){
					that.login({
						pin: param.pin,
						success: function () {
							seq.resolve();
						},
						fail:function (e) {
							seq.reject(e);
						}
					})
				}
				else{
					seq.resolve();
				}
			})
			.then(function () {
				//签名
				if(param.format && param.format === 'b64'){
					that.signB64({
						content: param.content,
						success: function (signature) {
							seq.resolve(signature);
						},
						fail: function (e) {
							seq.reject(e);
						}
					})
				}
				else{
					that.sign({
						content: param.content,
						success: function (signature) {
							seq.resolve(signature);
						},
						fail: function (e) {
							seq.reject(e);
						}
					})
				}
			})
			.then(function (s,signature) {
				//获取签名证书
				that.getCertB64({
					success: function (cert) {
						seq.resolve({
							cert: cert,
							signature: signature
						})
					},
					fail: function (e) {
						seq.reject(e);
					}
				})
			})
			.then(function (s,result) {
				//返回
				that._onSuccess(name,param,result);
			})
			.onerror(function (e) {
				that._onFail(name,param,e);
			})
			.run();
	};


	/**
	 * 非对称加密
	 * @param param
	 * {
	 * 		content: "" ,
	 *     success：function(encryptContent){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.encrypt=function (param) {
		var name="encrypt";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var encrypted = this._getCryptCert().EncryptB64(param.content);
				this._onSuccess(name,param,encrypted);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('EncryptB64','{"b64data":"'+ param.content +'"}',function (json) {
				that._onSuccess(name,param,json.b64encrypted);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 非对称解密
	 * @param param
	 * {
	 * 		content: "" ,   //密文
	 *     success：function(decryptContent){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.decrypt=function(param){
		var name="decrypt";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var decrypted = this._getCryptCert().DecryptB64(param.content);
				this._onSuccess(name,param,decrypted);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('DecryptB64','{"b64data":"'+ param.content +'"}',function (json) {
				that._onSuccess(name,param,json.b64decrypted);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 生成文件加密密钥
	 * @param param
	 * {
	 *     success：function(key){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.generateFileKeyB64=function(param){
		var name="generateFileKeyB64";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var b64key = this.util.GenerateFileKeyB64();
				this._onSuccess(name,param,b64key);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('GenerateFileKeyB64','{"b64data":"'+ param.content +'"}',function (json) {
				that._onSuccess(name,param,json.b64key);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 加密文件
	 * @param param
	 * {
	 *     key: '',
	 *     srcPath: '',
	 *     dstPath: '',
	 *     success：function(){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.encryptFile=function(param){
		var name="encryptFile";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				this.util.EncryptFile (param.key, param.srcPath, param.dstPath);
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('EncryptFile','{"key":"'+ param.key +'","src":"' + param.srcPath + '","dst":"' + param.dstPath + '"}',function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 解密文件
	 * @param param
	 * {
	 *     key: '',
	 *     srcPath: '',
	 *     dstPath: '',
	 *     success：function(){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.decryptFile=function(param){
		var name="decryptFile";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				this.util.DecryptFile(param.key, param.srcPath, param.dstPath);
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('DecryptFile','{"key":"'+ param.key +'","src":"' + param.srcPath + '","dst":"' + param.dstPath + '"}',function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * P7签名
	 * @param param
	 * {
	 * 	   content: "" ,
	 *     success：function(p7){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.p7Sign=function(param){
		var name="p7Sign";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var pkcs7=this.certMgr.CreatePkcs7();
				pkcs7.SetSignCert(this._getSignCert());
				var signature = pkcs7.SignB64(0, param.content, 4);
				this._onSuccess(name,param,signature);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('P7SignB64','{"b64data":"'+ param.content + '"}',function (json) {
				that._onSuccess(name,param,json.b64data);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * P7签名验证
	 * @param param
	 * {
	 * 	   pkcs7: "" ,
	 *     success：function(result){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.p7VerifySign=function(param){
		var name="p7VerifySign";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var pkcs7=this.certMgr.CreatePkcs7();
				var valid = pkcs7.VerifyB64(param.pkcs7);
				this._onSuccess(name,param,valid);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('P7VerifyB64','{"b64data":"'+ param.pkcs7 + '"}',function (json) {
				that._onSuccess(name,param,json.result);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * P7加密
	 * @param param
	 * {
	 * 	   content: "" ,
	 * 	   certs： Array,   //解密人证书列表
	 *     success：function(result){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.p7Envelop=function(param){
		var name="p7Envelop";
		this._debugFunBegin(name,param);

		if(!param.certs || param.certs.length==0){
			this._onFail(name,param,this._createError(ErrorMapping.ERR_NOT_CRYPT_CERT));
			return;
		}

		if(this.isIE){
			try{
				var pkcs7=this.certMgr.CreatePkcs7();
				for(var i=0;i<param.certs.length;i++){
					pkcs7.AddRecipientCert(param.certs[i]);
				}
				var signature = pkcs7.EnvelopB64(param.content, 0);
				this._onSuccess(name,param,signature);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var result='"certs":[';
			for(var i=0;i<param.certs.length;i++){
				result += '{"cert":"';
				result += param.certs[i];
				result += '"}';
				if( i != (param.certs.length-1))
					result += ',';
			}
			result += ']';

			var that=this;
			this._webSocketSend('EnvelopB64','{"b64data":"'+ param.content + '",' + result + '}',function (json) {
				that._onSuccess(name,param,json.b64data);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * P7解密
	 * @param param
	 * {
	 * 	   pkcs7: "" ,
	 *     success：function(result){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.p7UnEnvelop=function(param){
		var name="p7UnEnvelop";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var pkcs7=this.certMgr.CreatePkcs7();
				this.cert = this.cryptCert = this.certMgr.SelectEncryptCert();
				pkcs7.SetUnEnvelopCert(this._getCryptCert());
				var signature = pkcs7.UnEnvelopB64(param.pkcs7);
				this._onSuccess(name,param,signature);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('UnEnvelopB64','{"b64data":"'+ param.pkcs7 + '"}',function (json) {
				that._onSuccess(name,param,json.b64data);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};


	/**
	 * 读取注册表
	 * @param param
	 * {
	 *     key: '',
	 *     name: '',
	 *     success：function(value){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.readRegB64=function(param){
		var name="readRegB64";
		this._debugFunBegin(name,param);
		var b64= new $c.Base64();
		if(this.isIE){
			try{
				var v = this.util.ReadReg(b64.decode(param.key), b64.decode(param.name));
				if(typeof v === "number" ){
					this._onSuccess(name,param,v);
				}
				else {
					this._onSuccess(name, param, b64.encode(v));
				}
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('ReadRegB64','{"key":"'+ param.key + '","name":"' + param.name + '"}',function (json) {
				that._onSuccess(name,param,json.value);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 写注册表
	 * @param param
	 * {
	 *    key: '',
	 *    name: '',
	 *    value: '',
	 *    success：function(){}
	 *    fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.writeRegStringB64=function(param){
		var name="writeRegStringB64";
		this._debugFunBegin(name,param);
		var b64= new $c.Base64();
		if(this.isIE){
			try{
				this.util.WriteReg(b64.decode(param.key), b64.decode(param.name), b64.decode(param.value));
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('WriteRegB64','{"key":"'+ param.key + '","name":"' + param.name + '","value":"' + param.value + '"}',function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	HebcaP11Object.prototype.writeRegIntB64=function(param){
		var name="writeRegIntB64";
		this._debugFunBegin(name,param);
		var b64= new $c.Base64();
		if(this.isIE){
			try{
				this.util.WriteReg(b64.decode(param.key), b64.decode(param.name), param.value);
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('WriteRegB64','{"key":"'+ param.key + '","name":"' + param.name + '","value":' + param.value + '}',function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	HebcaP11Object.prototype.readReg=function(param){
		var name="readReg";
		this._debugFunBegin(name,param);
		var b64= new $c.Base64();
		var that=this;
		this.readRegB64({
			key: b64.encode(param.key),
			name: b64.encode(param.name),
			ignoreError: param.ignoreError,
			success: function (v) {
				if(typeof v === "number" ){
					that._onSuccess(name,param,v);
				}
				else {
					that._onSuccess(name,param,b64.decode(v));
				}
			},
			fail: function (e) {
				that._onFail(name,param,e);
			}
		});
	};

	HebcaP11Object.prototype.writeReg=function(param){
		var name="writeReg";
		this._debugFunBegin(name,param);
		var b64= new $c.Base64();
		var that=this;
		if(typeof param.value === 'number'){
			this.writeRegIntB64({
				key: b64.encode(param.key),
				name: b64.encode(param.name),
				value: param.value,
				ignoreError: param.ignoreError,
				success: function () {
					that._onSuccess(name,param);
				},
				fail: function (e) {
					that._onFail(name,param,e);
				}
			});
		}
		else{
			this.writeRegStringB64({
				key: b64.encode(param.key),
				name: b64.encode(param.name),
				value: b64.encode(param.value),
				ignoreError: param.ignoreError,
				success: function () {
					that._onSuccess(name,param);
				},
				fail: function (e) {
					that._onFail(name,param,e);
				}
			});
		}
	};

	/**
	 * 向key中写入数据
	 * @param param
	 * {
	 *     name:''.
	 *     data: '',
	 *     isPrivate: true|false,
	 *    success：function(){}
	 *    fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.writeDataB64=function(param){
		var name="writeDataB64";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				this._getCert().Device.WriteDataB64(param.name, param.data, param.isPrivate);
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('WriteDataB64','{"name":"'+ param.name + '","b64data":"' + param.data + '","isprivate":' + param.isPrivate + '}',function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 从key中读取数据
	 * @param param
	 * {
	 *     name:''.
	 *    success：function(data){}
	 *    fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.readDataB64=function(param){
		var name="readDataB64";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var data = this._getCert().Device.ReadDataB64(param.name);
				this._onSuccess(name,param,data);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('ReadDataB64','{"name":"'+ param.name + '"}',function (json) {
				that._onSuccess(name,param,json.b64data);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 向key中写入数据
	 * @param param
	 * {
	 *     name:''.
	 *     data: '',
	 *     isPrivate: true|false,
	 *    success：function(){}
	 *    fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.writeDataText=function(param){
		var name="writeDataText";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				this._getCert().Device.WriteDataText(param.name, param.data, param.isPrivate);
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('WriteDataText','{"name":"'+ param.name + '","data":"' + param.data + '","isprivate":' + param.isPrivate + '}',function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	HebcaP11Object.prototype.readDataText=function(param){
		var name="readDataText";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var data = this._getCert().Device.ReadDataText(param.name);
				this._onSuccess(name,param,data);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('ReadDataText','{"name":"'+ param.name + '"}',function (json) {
				that._onSuccess(name,param,json.data);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 *
	 * @param param
	 * {
	 *     name
	 * }
	 */
	HebcaP11Object.prototype.deleteData=function(param){
		var name="deleteData";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				this._getCert().Device.DeleteData(param.name);
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('DeleteData','{"name":"' + param.name + '"}',function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};



	/**
	 * 创建软证书设备
	 * @param param
	 * {
	 *     success：function(){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.createSM2SoftDevice=function(param) {
		var name="createSM2SoftDevice";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				this.device = this.certMgr.CreateSM2SoftDevice();
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('CreateSM2SoftDevice',null,function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});

		}
	};

	/**
	 * 生成密码对
	 * @param param
	 * {
	 *     certCN: '',
	 *     rsaFlag:
	 *     success：function(pubKeyB64){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.genKeyPair=function(param){
		var name="genKeyPair";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var pubKeyB64;
				if (this.device) {
					pubKeyB64 = this.device.GenKeyPair(param.certCN, param.rsaFlag);
				}
				else if (this.cert != null) {
					pubKeyB64 = this.cert.Device.GenKeyPair(param.certCN, param.rsaFlag);
				}
				else {
					this.device = this.certMgr.SelectDevice();
					pubKeyB64 = this.device.GenKeyPair(param.certCN, param.rsaFlag);
				}

				this._onSuccess(name,param,pubKeyB64);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('GenKeyPair','{"cn":"'+ param.certCN + '","rsaflag":' + param.rsaFlag + '}',function (json) {
				that._onSuccess(name,param,json.pubkeyb64);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 导入密钥对
	 * @param param
	 * {
	 *     keyPair: '',
	 *     success：function(){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.importECCKeyPair=function(param){
		var name="importECCKeyPair";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				if (this.device) {
					this.device.ImportECCKeyPair(param.keyPair);
				}
				else if (this.cert != null) {
					this.cert.Device.ImportECCKeyPair(param.keyPair);
				}
				else {
					this.device = this.certMgr.SelectDevice();
					this.device.ImportECCKeyPair(param.keyPair);
				}
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('ImportECCKeyPair','{"b64keys":"'+ param.keyPair + '"}',function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 导入证书
	 * @param param
	 * {
	 *     cert：‘’
	 *     signFlag: true|false
	 *     success：function(){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.importCert=function(param){
		var name="importCert";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				if (this.device) {
					this.device.ImportCert(param.cert, param.signFlag);
				}
				else if (this.cert != null) {
					this.cert.Device.ImportCert(param.cert, param.signFlag);
				}
				else {
					this.device = this.certMgr.SelectDevice();
					this.device.ImportCert(param.cert, param.signFlag);
				}
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('ImportCert','{"b64cert":"'+ param.cert + '","signflag":' + param.signFlag + '}',function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 在线格式化
	 * @param param
	 * {
	 *     url: '',
	 *     adminPin: '',
	 *     success：function(){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.onlineFormatByURL=function(param){
		var name="onlineFormatByURL";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				if (this.device) {
					this.device.OnlineFormatByURL(param.url, param.adminPin);
				}
				else if (this.cert != null) {
					this.cert.Device.OnlineFormatByURL(param.url, param.adminPin);
				}
				else {
					this.device = this.certMgr.SelectDevice();
					this.device.OnlineFormatByURL(param.url, param.adminPin);
				}
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('OnlineFormatByURL','{"url":"'+ param.url + '","adminpin":"' + param.adminPin + '"}',function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 在线解锁
	 * @param param
	 * {
	 *     url: '',
	 *     pin: '',
	 *     success：function(){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.onlineResetPinByURL=function(param){
		var name="onlineResetPinByURL";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				if (this.device) {
					this.device.OnlineResetPinByURL(param.url, param.pin);
				}
				else if (this.cert != null) {
					this.cert.Device.OnlineResetPinByURL(param.url, param.pin);
				}
				else {
					this.device = this.certMgr.SelectDevice();
					this.device.OnlineResetPinByURL(param.url, param.pin);
				}
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('OnlineResetPinByURL','{"url":"'+ param.url + '","userpin":"' + param.pin + '"}',function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 运行
	 * @param param
	 * {
	 *     filePath:''.
	 *     args:'',
	 *     wait:
	 *     success：function(){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.execute=function(param){
		var name="execute";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				this.util.Execute(param.filePath, param.args, param.wait);
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('Execute','{"filename":"'+ param.filePath + '","args":"' + param.args + '","wait":'+param.wait+'}',function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 * 关闭进程
	 * @param param
	 * {
	 *     name：'',
	 *     wait:
	 *     success：function(){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.killProcess=function(param){
		var name="killProcess";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var pid = this.util.FindProcessByModule(param.name);
				this.util.KillProcess(pid, param.wait);
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('KillProcess','{"name":"'+ param.name + '","wait":'+param.wait+'}',function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});

		}
	};

	HebcaP11Object.prototype.reloadDevice=function(param){
		var name="reloadDevice";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				this.certMgr.ReloadDevice();
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('ReloadDevice',null,function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	HebcaP11Object.prototype.deleteFromCertStore=function(param){
		var name="deleteFromCertStore";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				//！！！
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('DeleteFromCertStore',null,function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});
		}

	};

	HebcaP11Object.prototype.getCertList=function(param){
		var name="getCertList";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var count=this.certMgr.GetSignCertCount();
				var certList ="";
				var certSnList="";
				for(var i=0;i<count;i++) {
					var c=this.certMgr.GetSignCert(i);
					certSnList+=c.GetSerialNumberHex();
					certList+=c.GetCertB64();
					if(i!=count-1){
						certSnList+=";";
						certList+=";";
					}
				}

				this._onSuccess(name,param,{
					certList: certList,
					certSnList: certSnList
				});
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('GetCertList',null,function (json) {
				that._onSuccess(name,param,{
					certList: json.certlist,
					certSnList: json.certsnlist
				});
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 *
	 * @param param
	 * {
	 *     certSN, content, digestAlg,
	 *     success：function(digest){}
	 *     fail：function(e){}
	 * }
	 */
	HebcaP11Object.prototype.digestByCertSN=function(param){
		var name="digestByCertSN";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var digestAlg=param.digestAlg;
				var count=this.certMgr.GetSignCertCount();
				for(var i=0;i<count;i++){
					var c=this.certMgr.GetSignCert(i);
					if(param.certSN === c.GetSerialNumberHex()){
						var isSM2=c.IsSM2Cert;
						if(isSM2){
							digestAlg="sm3";
						}
						else{
							digestAlg="sha1";
						}
						break;
					}
				}

				digest=this.util.HashB64(param.content,digestAlg==="sm3"?2:1);
				this._onSuccess(name,param,{
					digest: digest,
					digestAlg:digestAlg
				});
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			var that=this;
			this._webSocketSend('Digest','{"certSN":"' + param.certSN + '","b64data":"' + param.content + '","digestalg":"' + param.digestAlg + '"}',function (json) {
				that._onSuccess(name,param,{
					digest: json.digest,
					digestAlg: json.digestalg
				});
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	HebcaP11Object.prototype.getMachineInfo=function(param){
		var name="getMachineInfo";
		this._debugFunBegin(name,param);
		if(this.isIE){
			this._onFail(name,param,this._createError(ErrorMapping.ERR_NOT_SUPPORT_FUN));
		}else{
			var that=this;
			this._webSocketSend('GetMachineInfo',null,function (json) {
				that._onSuccess(name,param,{
					ip: json.ip,
					mac: json.mac
				});
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};


	/**
	 * \
	 * @param param
	 * {
	 *     signFlag
	 * }
	 */
	HebcaP11Object.prototype.getPubKeyData=function(param){
		var name="getPubKeyData";
		this._debugFunBegin(name,param);
		if(this.isIE){
			this._onFail(name,param,this._createError(ErrorMapping.ERR_NOT_SUPPORT_FUN));
		}else{
			var that=this;
			this._webSocketSend('GetPubKeyData','{"signflag":'+ param.signFlag + '}',function (json) {
				that._onSuccess(name,param,json.pubkey);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 *
	 * @param param
	 * {
	 *     alg, sponsorId
	 * }
	 */
	HebcaP11Object.prototype.generateAgreementDataWithECC=function(param){
		var name="generateAgreementDataWithECC";
		this._debugFunBegin(name,param);
		if(this.isIE){
			this._onFail(name,param,this._createError(ErrorMapping.ERR_NOT_SUPPORT_FUN));
		}else{
			var that=this;
			var b64=new $c.Base64();
			this._webSocketSend('GenerateAgreementDataWithECC','{"alg":"' + param.alg + '","sponsorid":"' + b64.encode(param.sponsorId) + '"}',function (json) {
				that._onSuccess(name,param,json.sponsortemppubkey);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 *
 	 * @param param
	 * {
	 *     alg, sponsorPubKey, sponsorTempPubKey, respondId, sponsorId
	 * }
	 */
	HebcaP11Object.prototype.generateAgreementDataAndKeyWithECC=function(param){
		var name="generateAgreementDataAndKeyWithECC";
		this._debugFunBegin(name,param);
		if(this.isIE){
			this._onFail(name,param,this._createError(ErrorMapping.ERR_NOT_SUPPORT_FUN));
		}else{
			var that=this;
			var b64=new $c.Base64();
			this._webSocketSend('GenerateAgreementDataAndKeyWithECC','{"alg":"'+param.alg+'","sponsorpubkey":"'+param.sponsorPubKey+'","sponsortemppubkey":"'+param.sponsorTempPubKey+'","respondid":"'+ b64.encode(param.respondId)+'","sponsorid":"'+b64.encode(param.sponsorId)+'"}',function (json) {
				that._onSuccess(name,param,json.respondtemppubkey);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 *
	 * @param param
	 * {
	 *     respondPubKey, respondTempPubKey, respondId,
	 * }
	 */
	HebcaP11Object.prototype.generateKeyWithECC=function(param){
		var name="generateKeyWithECC";
		this._debugFunBegin(name,param);
		if(this.isIE){
			this._onFail(name,param,this._createError(ErrorMapping.ERR_NOT_SUPPORT_FUN));
		}else{
			var that=this;
			var b64=new $c.Base64();
			this._webSocketSend('GenerateKeyWithECC','{"respondpubkey":"'+param.respondPubKey+'","respondtemppubkey":"'+param.respondTempPubKey+'","respondid":"'+ b64.encode(param.respondId)+'"}',function (json) {
				that._onSuccess(name,param);
			},function (e) {
				that._onFail(name,param,e);
			});

		}
	};

	/**
	 *
	 * @param param
	 * {
	 *     content
	 * }
	 */
	HebcaP11Object.prototype.encryptWithAgreementKey=function(param){
		var name="encryptWithAgreementKey";
		this._debugFunBegin(name,param);
		if(this.isIE){
			this._onFail(name,param,this._createError(ErrorMapping.ERR_NOT_SUPPORT_FUN));
		}else{
			var that=this;
			this._webSocketSend('EncryptWithAgreementKey','{"plaintext":"'+ param.content + '"}',function (json) {
				that._onSuccess(name,param,json.ciphertext);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};

	/**
	 *
	 * @param param
	 * {
	 *     content
	 * }
	 */
	HebcaP11Object.prototype.decryptWithAgreementKey=function(param){
		var name="decryptWithAgreementKey";
		this._debugFunBegin(name,param);
		if(this.isIE){
			this._onFail(name,param,this._createError(ErrorMapping.ERR_NOT_SUPPORT_FUN));
		}else{
			var that=this;
			this._webSocketSend('DecryptWithAgreementKey','{"ciphertext":"'+ param.content + '"}',function (json) {
				that._onSuccess(name,param,json.plaintext);
			},function (e) {
				that._onFail(name,param,e);
			});
		}
	};



	/**
	 * 获取助手版本
	 * @param param
	 *  success: function(version){}
	 *  fail: function(e){}
	 *
	 */
	HebcaP11Object.prototype.getAssistorVersion=function (param) {
		var name="getAssistorVersion";
		this._debugFunBegin(name,param);
		var that=this;
		this.readReg({
			key: "\\HKEY_CURRENT_USER\\Software\\Hebca\\ClientX",
			name: "version",
			success: function (v) {
				that._onSuccess(name,param,v);
			},
			fail: function (e) {
				that._onFail(name,param,e);
			}
		});
	};

	/**
	 * 验证助手版本是否等于或者高于某个版本
	 * @param param
	 * {
	 *     version: xx ,  整数， 比如6.3.5， 写作 0x060305
	 *
	 * }
	 */
	HebcaP11Object.prototype.assertAssistorVersion=function (param) {
		var name="assertAssistorVersion";
		this._debugFunBegin(name,param);
		var that=this;
		this.getAssistorVersion({
			success: function (v) {
				that._onSuccess(name, param,v >= param.version );
			},
			fail: function (e) {
				that._onFail(name,param,e);
			}
		})
	};

	/**
	 * 遍历所有证书
	 * @param param
	 * {
	 *     fun: function(index,resolve,reject){  } 遍历函数
	 *     success: function(){}
	 *     fail: function(e){}
	 * }
	 */
	HebcaP11Object.prototype.forEachSignCert=function (param) {
		var name="forEachSignCert";
		this._debugFunBegin(name,param);
		var seq=new $c.Seq();
		var that=this;
		var certsIndex=[];
		seq.then(function () {
			that.getSignCertCount({
				success: function (count) {
					for(var i=0;i<count;i++){
						certsIndex.push(i);
					}

					seq.firstEach(certsIndex,function (s,index) {
						var seq1=new $c.Seq();
						seq1.then(function () {
							that.getSignCert({
								index: index,
								success: function () {
									seq1.resolve();
								},
								fail: function (e) {
									seq1.reject(e);
								}
							})
						})
							.then(function () {
								param.fun(index,function (v) {
									seq1.resolve(v)
								},function (e) {
									seq1.reject(e);
								});
							})
							.then(function (v) {
								seq.resolve(index+1);
							})
							.onerror(function (e) {
								seq.reject(e)
							})
							.run();

					});

					seq.resolve(0);
				},
				fail: function (e) {
					seq.reject(e);
				}
			});
		})
			.then(function () {
				that._onSuccess(name,param);
			})
			.onerror(function (e) {
				that._onFail(name,param,e);
			})
			.run();
	};

	HebcaP11Object.prototype.epsImportSymmKeyB64=function (param) {
		var name="epsImportSymmKeyB64";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				this._getCert().Device.EPSImportSymmKeyB64(param.keyIndex, param.key);
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			this._onFail(name,param,this._createError(ErrorMapping.ERR_NOT_SUPPORT_FUN));
		}
	};

	HebcaP11Object.prototype.epsWriteESealDataB64=function (param) {
		var name="epsWriteESealDataB64";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				this._getCert().Device.EPSWriteESealDataB64(param.sealData);
				this._onSuccess(name,param);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			this._onFail(name,param,this._createError(ErrorMapping.ERR_NOT_SUPPORT_FUN));
		}
	};

	HebcaP11Object.prototype.epsReadESealDataB64=function (param) {
		var name="epsReadESealDataB64";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var sealData=this._getCert().Device.EPSReadESealDataB64(param.keyIndex,param.algId);
				this._onSuccess(name,param,sealData);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			this._onFail(name,param,this._createError(ErrorMapping.ERR_NOT_SUPPORT_FUN));
		}
	};

	HebcaP11Object.prototype.epsEncryptB64=function (param) {
		var name="epsEncryptB64";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var outputData=this._getCert().Device.EPSEncryptB64(param.keyIndex,param.algId, param.iv, param.divCount, param.divComponent, param.inputData);
				this._onSuccess(name,param,outputData);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			this._onFail(name,param,this._createError(ErrorMapping.ERR_NOT_SUPPORT_FUN));
		}
	};

	HebcaP11Object.prototype.epsDecryptB64=function (param) {
		var name="epsDecryptB64";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var outputData=this._getCert().Device.EPSDecryptB64(param.keyIndex,param.algId, param.iv, param.divCount, param.divComponent, param.inputData);
				this._onSuccess(name,param,outputData);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			this._onFail(name,param,this._createError(ErrorMapping.ERR_NOT_SUPPORT_FUN));
		}
	};

	HebcaP11Object.prototype.epsMacB64=function (param) {
		var name="epsMacB64";
		this._debugFunBegin(name,param);
		if(this.isIE){
			try{
				var outputData=this._getCert().Device.EPSMacB64(param.keyIndex, param.algId, param.iv, param.divCount, param.divComponent, param.inputData);
				this._onSuccess(name,param,outputData);
			}catch(e){
				this._onFail(name,param,e);
			}
		}else{
			this._onFail(name,param,this._createError(ErrorMapping.ERR_NOT_SUPPORT_FUN));
		}
	};

	/**
	 *
	 * @param param
	 * @constructor
	 */
	function Seq(param) {
        this.seq_funs=null;
        if(param){
            if(Util.isArray(param)){
                this.seq_funs=new Array();
                for(var i=0;i<param.length;i++){
                    this.seq_funs.push(param[i]);
                }
            }
            else{
                this.seq_funs=new Array();
                this.seq_funs.push(param);
            }
        }
        else{
            this.seq_funs=new Array();
        }
    }

	function isArray(value){
		if (typeof Array.isArray === "function") {
			return Array.isArray(value);
		}else{
			return Object.prototype.toString.call(value) === "[object Array]";
		}
	}

    Seq.prototype.then=function(param){
        if(isArray(param)){
            for(var i=0;i<param.length;i++){
                this.seq_funs.push(param[i]);
            }
        }
        else{
            this.seq_funs.push(param);
        }
        return this;
    };

    Seq.prototype.first=function(param){
        if(isArray(param)){
            for(var i=param.length-1;i>=0;i--){
                this.seq_funs.unshift(param[i]);
            }
        }
        else{
            this.seq_funs.unshift(param);
        }
        return this;
    };

    Seq.prototype.firstEach=function(items,fun){
        var funs=[];
        for(var i=0;i<items.length;i++){
            funs.push(fun);
        }
        return this.first(funs);
    };

    Seq.prototype.thenEach=function(items,fun){
        var funs=[];
        for(var i=0;i<items.length;i++){
            funs.push(fun);
        }
        return this.then(funs);
    };

    Seq.prototype.run=function(param){
        var fun=this.seq_funs.shift();
        if(fun){
            try{
                fun(this,param);
            }
            catch(e){
                if(this.errorFun)
                    this.errorFun(e);
                else
                    throw e;
            }
        }
    };

    Seq.prototype.resolve=function(param){
        var fun=this.seq_funs.shift();
        if(fun){
            try{
                fun(this,param);
            }
            catch(e){
                if(this.errorFun)
                    this.errorFun(e);
                else
                    throw e;
            }
        }
    };

    Seq.prototype.reject=function(e){
		if(this.errorFun)
			this.errorFun(e);
		else
			throw e;
	};

    Seq.prototype.onerror=function(fun){
        this.errorFun=fun;
        return this;
    };

    function HebcaP11ObjectX(param){
    	if(param && param.sign){
			this.p11Object=param;
		}
    	else{
    		var success=param.success;
    		param.success=function (client) {
				if(success){
					success(new HebcaP11ObjectX(client));
				}
			};
			this.p11Object=new $c.HebcaP11Object(param);
		}
	}

	//将函数转化为Promise
	function toPromise(func,param){
    	param = param || {};
		return new Promise(function (resolve,reject) {
			param.success=function (ret) {
				resolve(ret);
			};
			param.fail=function (msg) {
				reject(new Error(msg));
			};
			func(param);
		});
	}

	function toPromiseWithObj(obj,func,param){
		param = param || {};
		return new Promise(function (resolve,reject) {
			param.success=function (ret) {
				resolve(ret);
			};
			param.fail=function (msg) {
				reject(new Error(msg));
			};
			func.call(obj,param);
		});
	}

	HebcaP11Object.prototype.getP11Object=function(){
    	return this.p11Object;
	};
	HebcaP11ObjectX.prototype.setDebug=function(d){
    	this.p11Object.setDebug(d);
	};
    HebcaP11ObjectX.prototype.debug=function(){
		this.p11Object.debug.apply(this, arguments);
	};
	HebcaP11ObjectX.prototype.uiLogin=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.uiLogin,param);
	};
	HebcaP11ObjectX.prototype.sign=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.sign,param);
	};
	HebcaP11ObjectX.prototype.selectSignCert=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.selectSignCert,param);
	};
	HebcaP11ObjectX.prototype.selectCryptCert=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.selectCryptCert,param);
	};
	HebcaP11ObjectX.prototype.getSignCert=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.getSignCert,param);
	};
	HebcaP11ObjectX.prototype.getSubjectItem=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.getSubjectItem,param);
	};
	HebcaP11ObjectX.prototype.getCertB64=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.getCertB64,param);
	};
	HebcaP11ObjectX.prototype.getDeviceCount=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.getDeviceCount,param);
	};
	HebcaP11ObjectX.prototype.getCertCount=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.getCertCount,param);
	};
	HebcaP11ObjectX.prototype.encrypt=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.encrypt,param);
	};
	HebcaP11ObjectX.prototype.decrypt=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.decrypt,param);
	};
	HebcaP11ObjectX.prototype.generateFileKeyB64=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.generateFileKeyB64,param);
	};
	HebcaP11ObjectX.prototype.encryptFile=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.encryptFile,param);
	};
	HebcaP11ObjectX.prototype.decryptFile=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.decryptFile,param);
	};
	HebcaP11ObjectX.prototype.p7Sign=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.p7Sign,param);
	};
	HebcaP11ObjectX.prototype.p7VerifySign=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.p7VerifySign,param);
	};
	HebcaP11ObjectX.prototype.p7Envelop=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.p7Envelop,param);
	};
	HebcaP11ObjectX.prototype.p7UnEnvelop=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.p7UnEnvelop,param);
	};
	HebcaP11ObjectX.prototype.isCertOnline=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.isCertOnline,param);
	};
	HebcaP11ObjectX.prototype.signB64=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.signB64,param);
	};
	HebcaP11ObjectX.prototype.getSignCertCount=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.getSignCertCount,param);
	};
	HebcaP11ObjectX.prototype.login=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.login,param);
	};
	HebcaP11ObjectX.prototype.cspLogin=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.cspLogin,param);
	};
	HebcaP11ObjectX.prototype.getSignCertNotAfter=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.getSignCertNotAfter,param);
	};
	HebcaP11ObjectX.prototype.readRegB64=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.readRegB64,param);
	};
	HebcaP11ObjectX.prototype.writeRegStringB64=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.writeRegStringB64,param);
	};
	HebcaP11ObjectX.prototype.writeRegIntB64=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.writeRegIntB64,param);
	};
	HebcaP11ObjectX.prototype.writeDataB64=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.writeDataB64,param);
	};
	HebcaP11ObjectX.prototype.readDataB64=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.readDataB64,param);
	};
	HebcaP11ObjectX.prototype.writeDataText=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.writeDataText,param);
	};
	HebcaP11ObjectX.prototype.readDataText=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.readDataText,param);
	};
	HebcaP11ObjectX.prototype.deleteData=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.deleteData,param);
	};
	HebcaP11ObjectX.prototype.isCertLogined=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.isCertLogined,param);
	};
	HebcaP11ObjectX.prototype.logout=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.logout,param);
	};
	HebcaP11ObjectX.prototype.createSM2SoftDevice=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.createSM2SoftDevice,param);
	};
	HebcaP11ObjectX.prototype.genKeyPair=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.genKeyPair,param);
	};
	HebcaP11ObjectX.prototype.importECCKeyPair=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.importECCKeyPair,param);
	};
	HebcaP11ObjectX.prototype.importCert=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.importCert,param);
	};
	HebcaP11ObjectX.prototype.onlineFormatByURL=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.onlineFormatByURL,param);
	};
	HebcaP11ObjectX.prototype.onlineResetPinByURL=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.onlineResetPinByURL,param);
	};
	HebcaP11ObjectX.prototype.execute=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.execute,param);
	};
	HebcaP11ObjectX.prototype.killProcess=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.killProcess,param);
	};
	HebcaP11ObjectX.prototype.reloadDevice=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.reloadDevice,param);
	};
	HebcaP11ObjectX.prototype.deleteFromCertStore=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.deleteFromCertStore,param);
	};
	HebcaP11ObjectX.prototype.selectSignCertFilterDevice=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.selectSignCertFilterDevice,param);
	};
	HebcaP11ObjectX.prototype.getCertList=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.getCertList,param);
	};
	HebcaP11ObjectX.prototype.digestByCertSN=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.digestByCertSN,param);
	};
	HebcaP11ObjectX.prototype.getMachineInfo=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.getMachineInfo,param);
	};
	HebcaP11ObjectX.prototype.unUICertLogin=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.unUICertLogin,param);
	};
	HebcaP11ObjectX.prototype.unUISign=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.unUISign,param);
	};
	HebcaP11ObjectX.prototype.getPubKeyData=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.getPubKeyData,param);
	};
	HebcaP11ObjectX.prototype.generateAgreementDataWithECC=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.generateAgreementDataWithECC,param);
	};
	HebcaP11ObjectX.prototype.generateAgreementDataAndKeyWithECC=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.generateAgreementDataAndKeyWithECC,param);
	};
	HebcaP11ObjectX.prototype.generateKeyWithECC=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.generateKeyWithECC,param);
	};
	HebcaP11ObjectX.prototype.encryptWithAgreementKey=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.encryptWithAgreementKey,param);
	};
	HebcaP11ObjectX.prototype.decryptWithAgreementKey=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.decryptWithAgreementKey,param);
	};
	HebcaP11ObjectX.prototype.readReg=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.readReg,param);
	};
	HebcaP11ObjectX.prototype.writeReg=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.writeReg,param);
	};
	HebcaP11ObjectX.prototype.getAssistorVersion=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.getAssistorVersion,param);
	};
	HebcaP11ObjectX.prototype.assertAssistorVersion=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.assertAssistorVersion,param);
	};
	HebcaP11ObjectX.prototype.signContent=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.signContent,param);
	};
	HebcaP11ObjectX.prototype.forEachSignCert=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.forEachSignCert,param);
	};
	HebcaP11ObjectX.prototype.selectSignCertBySubjectItem=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.selectSignCertBySubjectItem,param);
	};
	HebcaP11ObjectX.prototype.selectSignCertCombine=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.selectSignCertCombine,param);
	};
	HebcaP11ObjectX.prototype.epsImportSymmKeyB64=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.epsImportSymmKeyB64,param);
	};

	HebcaP11ObjectX.prototype.epsWriteESealDataB64=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.epsWriteESealDataB64,param);
	};

	HebcaP11ObjectX.prototype.epsReadESealDataB64=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.epsReadESealDataB64,param);
	};

	HebcaP11ObjectX.prototype.epsEncryptB64=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.epsEncryptB64,param);
	};

	HebcaP11ObjectX.prototype.epsDecryptB64=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.epsDecryptB64,param);
	};

	HebcaP11ObjectX.prototype.epsMacB64=function (param) {
		return toPromiseWithObj(this.p11Object,this.p11Object.epsMacB64,param);
	};

	function Base64() {
		// private property
		_keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

		// public method for encoding
		this.encode = function (input) {
			var output = "";
			var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
			var i = 0;
			input = _utf8_encode(input);
			while (i < input.length) {
				chr1 = input.charCodeAt(i++);
				chr2 = input.charCodeAt(i++);
				chr3 = input.charCodeAt(i++);
				enc1 = chr1 >> 2;
				enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
				enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
				enc4 = chr3 & 63;
				if (isNaN(chr2)) {
					enc3 = enc4 = 64;
				} else if (isNaN(chr3)) {
					enc4 = 64;
				}
				output = output +
					_keyStr.charAt(enc1) + _keyStr.charAt(enc2) +
					_keyStr.charAt(enc3) + _keyStr.charAt(enc4);
			}
			return output;
		};

		// public method for decoding
		this.decode = function (input) {
			var output = "";
			var chr1, chr2, chr3;
			var enc1, enc2, enc3, enc4;
			var i = 0;
			input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");
			while (i < input.length) {
				enc1 = _keyStr.indexOf(input.charAt(i++));
				enc2 = _keyStr.indexOf(input.charAt(i++));
				enc3 = _keyStr.indexOf(input.charAt(i++));
				enc4 = _keyStr.indexOf(input.charAt(i++));
				chr1 = (enc1 << 2) | (enc2 >> 4);
				chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
				chr3 = ((enc3 & 3) << 6) | enc4;
				output = output + String.fromCharCode(chr1);
				if (enc3 != 64) {
					output = output + String.fromCharCode(chr2);
				}
				if (enc4 != 64) {
					output = output + String.fromCharCode(chr3);
				}
			}
			output = _utf8_decode(output);
			return output;
		};

		// private method for UTF-8 encoding
		_utf8_encode = function (string) {
			string = string.replace(/\r\n/g,"\n");
			var utftext = "";
			for (var n = 0; n < string.length; n++) {
				var c = string.charCodeAt(n);
				if (c < 128) {
					utftext += String.fromCharCode(c);
				} else if((c > 127) && (c < 2048)) {
					utftext += String.fromCharCode((c >> 6) | 192);
					utftext += String.fromCharCode((c & 63) | 128);
				} else {
					utftext += String.fromCharCode((c >> 12) | 224);
					utftext += String.fromCharCode(((c >> 6) & 63) | 128);
					utftext += String.fromCharCode((c & 63) | 128);
				}

			}
			return utftext;
		};

		// private method for UTF-8 decoding
		_utf8_decode = function (utftext) {
			var string = "";
			var i = 0;
			var c = 0;
			var c1 = 0;
			var c2 = 0;
			while ( i < utftext.length ) {
				c = utftext.charCodeAt(i);
				if (c < 128) {
					string += String.fromCharCode(c);
					i++;
				} else if((c > 191) && (c < 224)) {
					c2 = utftext.charCodeAt(i+1);
					string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
					i += 2;
				} else {
					c2 = utftext.charCodeAt(i+1);
					var c3 = utftext.charCodeAt(i+2);
					string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
					i += 3;
				}
			}
			return string;
		};
	}

	function createP11ObjectX(param){
		return new Promise(function (resolve,reject){
			param.success=function (client) {
				resolve(client);
			};
			param.fail=function (e) {
				reject(e);
			};
			new HebcaP11ObjectX(param);
		});
	}

	//export
    $c.HebcaP11Object=HebcaP11Object;
	$c.Seq=Seq;
	$c.HebcaP11ObjectX=HebcaP11ObjectX;
	$c.Base64=Base64;
	$c.VersionInfo=VersionInfo;
	$c.createP11ObjectX=createP11ObjectX;

})(HebcaClient);
