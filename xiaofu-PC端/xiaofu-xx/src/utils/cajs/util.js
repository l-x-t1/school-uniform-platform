
var userUI={
    selectCert: function (param) {
        param.success(param.certs[0]);
    },
    selectDevice:function (param) {
        param.success(param.devices[0]);
    },
    loginCert:function (param){
        param.success("123456");
    },
    loginDevice:function (param){
        param.success("123456");
    },
    selectSeal:function (param){
        param.success(param.seals[0]);
    },
    loginSeal:function (param){
        param.success("123456");
    }
};

function setUi() {
    var ui=HebcaMiddleware.util.getLocal("hebca_ui");
    if(ui){
        if(ui==="YS"){
            HebcaMiddleware.setUi(null);
        }
        else if(ui==="JS"){
            HebcaMiddleware.setUi(HebcaMiddleware.uiBootstrap);
        }
        else{
            HebcaMiddleware.setUi(userUI);
        }
    }
}

function getRememberCert(){
    var r=HebcaMiddleware.util.getLocal("hebca_rememberCert")
    if(r && r==="true"){
        return true
    } else {
        return false
    }
}

function getIeVersionActivex(){
    return 12
}

function showJson(elem,data) {
    document.getElementById(elem).innerText=JSON.stringify(data,null,2);
    document.getElementById(elem).style.display="block";
}

function showCode(elem,data) {
    document.getElementById(elem).innerText=data;
    document.getElementById(elem+"_container").style.display="block";
    document.getElementById(elem).style.display="block";
    document.getElementById(elem+"_button").style.display="block";
    hljs.highlightAll();
}

function checkEndsWith(){
    if (typeof String.prototype.endsWith != 'function') {
        String.prototype.endsWith = function(suffix) {
            return this.indexOf(suffix, this.length - suffix.length) !== -1;
        };
    }
}

function initHeaderMenu(){
    var menu=[
        {
            name: "证书登录",
            url:"demo.html",
        },
    ]

    var headerMenu=$("#headerMenu");
    for(var i=0;i<menu.length;i++){
        if(menu[i].dev){
            headerMenu.append("<li><a href='"+menu[i].url+"' style='color: grey' title='此功能正在开发中...'>"+menu[i].name+"</a></li>")
        }else{
            headerMenu.append("<li><a href='"+menu[i].url+"'>"+menu[i].name+"</a></li>")
        }
    }

    $("#demoVersion").text(demoVersion)
}

var demoVersion = "V1.3.0"
