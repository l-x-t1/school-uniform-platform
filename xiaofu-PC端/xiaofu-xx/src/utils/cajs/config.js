
//windows amd64
/*var fileRoot="d:\\";
var filePathSep="\\"
var demoServer="http://localhost:8081"
var testMoudleDownloadUrl="http://localhost:8081/demo/downloadPackage/YZT-windows-amd64-1.1.0-test-1.1.0.zip"
*/

var testConfigs={
    "windows": {
        fileRoot : "d:\\",
        filePathSep: "\\",
        demoServer: "http://localhost:8081"
    },
    "ubuntu-1": {
        fileRoot : "/home/echo/Desktop/",
        filePathSep: "/",
        demoServer: "http://127.0.0.1:8081"
    },
    "uos-arm64": {
        fileRoot : "/home/test/Desktop/Go/",
        filePathSep: "/",
        demoServer: "http://127.0.0.1:8081"
    },
	"kylin-amd64":{
		fileRoot : "/home/test/桌面/new/",
        filePathSep: "/",
        demoServer: "http://127.0.0.1:8081"
	}
}

//windows 386
var test_config= HebcaMiddleware.util.getLocal("test.config")
if(!test_config){
    test_config="windows"
}

var fileRoot=testConfigs[test_config].fileRoot;
var filePathSep=testConfigs[test_config].filePathSep;
var demoServer=testConfigs[test_config].demoServer;


//linux ubuntu
//var fileRoot="/home/echo/Desktop/"
//var filePathSep="/"
//var demoServer="http://127.0.0.1:8081"

//uos arm64
/*var fileRoot="/home/test/Desktop/Go/"
var filePathSep="/"
var demoServer="http://127.0.0.1:8081"
*/

//uos mips64
/*var fileRoot="/home/test/Desktop/Go/test/"
var filePathSep="/"
var demoServer="http://127.0.0.1:8081"
*/

//uos amd64
/*var fileRoot="/home/huoyanhui/Desktop/Go/test/"
var filePathSep="/"
var demoServer="http://127.0.0.1:8081"
*/

//kylin amd64
/*var fileRoot="/home/wangyang/桌面/Go/"
var filePathSep="/"
var demoServer="http://127.0.0.1:8081"
*/

//kylin arm64
/*var fileRoot="/home/kylin/Go/"
var filePathSep="/"
var demoServer="http://127.0.0.1:8081"
*/

//neokylin mips64le
/*var fileRoot="/home/sugon/桌面/Go/test/"
var filePathSep="/"
var demoServer="http://127.0.0.1:8081"
*/

var keyPassword="123456";
var sealSnInCloud="Seal1387005489043521536"
var certIdInCloud="服务端证书一153530"
var certPasswordInCloud="123456"
var pdfServerFileSrc="1.pdf"
var pdfServerFileDest="2.pdf"
var ofdServerFileSrc="1.ofd"
var ofdServerFileDest="2.ofd"
var testMoudleVersion="1.1.0"
var agreementCertType="sign"

function pathJoin(){
    var file=""
    for(var i=0;i<arguments.length;i++){
        file+=arguments[i]
        if(i!==arguments.length-1){
            file+=filePathSep
        }
    }
    return file
}
