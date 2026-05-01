var fileSealNetTestSuit = new TestSuit("PDF签章(网络版)");

fileSealNetTestSuit.AddTest({
    name: "创建签章环境",
    asyn: true,
    fun: function(testResult){
        test_createContext(testResult,context_net)
    }
});

fileSealNetTestSuit.AddTest({
    name: "创建文件签章环境",
    asyn: true,
    fun: function(testResult){
        test_createFileSealContext(testResult,context_net)
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF联合签章-坐标",
    asyn: true,
    fun: function(testResult){
        test_pfdSignSealHash(testResult,context_net,"&sealType=0&type=0&page=1&pos=100,100")
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF联合签章-关键字",
    asyn: true,
    fun: function(testResult){
        test_pfdSignSealHash(testResult,context_net,"&sealType=0&type=1&page=1&keyword=Hello")
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF联合签章-骑缝",
    asyn: true,
    fun: function(testResult){
        test_pfdSignSealHash(testResult,context_net,"&sealType=0&type=2&offset=100")
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF联合签章-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_pfdSignSealHash(testResult,context_net,"&sealType=0&type=3&offset=100")
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF联合签章-签名域",
    asyn: true,
    fun: function(testResult){
        test_pfdSignSealHash(testResult,context_net,"&sealType=0&type=4","signField.pdf")
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF联合文字签批-坐标",
    asyn: true,
    fun: function(testResult){
        test_pfdSignSealHashQP(testResult,context_net,"&sealType=1&type=0&page=1&pos=100,100")
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF联合文字签批-关键字",
    asyn: true,
    fun: function(testResult){
        test_pfdSignSealHashQP(testResult,context_net,"&sealType=1&type=1&page=1&keyword=Hello")
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF联合文字签批-骑缝",
    asyn: true,
    fun: function(testResult){
        test_pfdSignSealHashQP(testResult,context_net,"&sealType=1&type=2&offset=100")
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF联合文字签批-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_pfdSignSealHashQP(testResult,context_net,"&sealType=1&type=3&offset=100")
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF联合文字签批-签名域",
    asyn: true,
    fun: function(testResult){
        test_pfdSignSealHashQP(testResult,context_net,"&sealType=1&type=4","signField.pdf")
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF联合图片签批-坐标",
    asyn: true,
    fun: function(testResult){
        test_pfdSignSealHashQP(testResult,context_net,"&sealType=2&type=0&page=1&pos=100,100")
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF联合图片签批-关键字",
    asyn: true,
    fun: function(testResult){
        test_pfdSignSealHashQP(testResult,context_net,"&sealType=2&type=1&page=1&keyword=Hello")
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF联合图片签批-骑缝",
    asyn: true,
    fun: function(testResult){
        test_pfdSignSealHashQP(testResult,context_net,"&sealType=2&type=2&offset=100")
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF联合图片签批-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_pfdSignSealHashQP(testResult,context_net,"&sealType=2&type=3&offset=100")
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF联合图片签批-签名域",
    asyn: true,
    fun: function(testResult){
        test_pfdSignSealHashQP(testResult,context_net,"&sealType=2&type=4","signField.pdf")
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF客户端盖章-坐标",
    asyn: true,
    fun: function(testResult){
        test_signClientFile(testResult,context_net,{
            type: 0,
            pos: {
                page: "1",
                coordinate: "100,100"
            }
        },0,false)
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF客户端盖章-关键字",
    asyn: true,
    fun: function(testResult){
        test_signClientFile(testResult,context_net,{
            type: 1,
            keyword: {
                page: "1",
                keyword: "Hello",
                offset: "0,0"
            }
        },0,false)
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF客户端盖章-骑缝",
    asyn: true,
    fun: function(testResult){
        test_signClientFile(testResult,context_net,{
            type: 3,
            cuttingRide: {
                mode: "oneSide",
                position: "right",
            }
        },0,false)
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF客户端盖章-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_signClientFile(testResult,context_net,{
            type: 3,
            cuttingRide: {
                mode: "opposite",
                position: "right",
            }
        },0,false)
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF客户端盖章-签名域",
    asyn: true,
    fun: function(testResult){
        test_signClientFile(testResult,context_net,{
            type: 2,
            signField: {
                allSignField: true,
            }
        },0,false,"signField.pdf")
    }
});


fileSealNetTestSuit.AddTest({
    name: "PDF客户端文字签批-坐标",
    asyn: true,
    fun: function(testResult){
        test_signClientFileQP(testResult,context_net,{
            type: 0,
            pos: {
                page: "1",
                coordinate: "100,100"
            }
        },1,false)
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF客户端文字签批-关键字",
    asyn: true,
    fun: function(testResult){
        test_signClientFileQP(testResult,context_net,{
            type: 1,
            keyword: {
                page: "1",
                keyword: "Hello",
                offset: "0,0"
            }
        },1,false)
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF客户端文字签批-骑缝",
    asyn: true,
    fun: function(testResult){
        test_signClientFileQP(testResult,context_net,{
            type: 3,
            cuttingRide: {
                mode: "oneSide",
                position: "right",
            }
        },1,false)
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF客户端文字签批-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_signClientFileQP(testResult,context_net,{
            type: 3,
            cuttingRide: {
                mode: "opposite",
                position: "right",
            }
        },1,false)
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF客户端文字签批-签名域",
    asyn: true,
    fun: function(testResult){
        test_signClientFileQP(testResult,context_net,{
            type: 2,
            signField: {
                allSignField: true,
            }
        },1,false,"signField.pdf")
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF客户端图片签批-坐标",
    asyn: true,
    fun: function(testResult){
        test_signClientFileQP(testResult,context_net,{
            type: 0,
            pos: {
                page: "1",
                coordinate: "100,100"
            }
        },2,false)
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF客户端图片签批-关键字",
    asyn: true,
    fun: function(testResult){
        test_signClientFileQP(testResult,context_net,{
            type: 1,
            keyword: {
                page: "1",
                keyword: "Hello",
                offset: "0,0"
            }
        },2,false)
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF客户端图片签批-骑缝",
    asyn: true,
    fun: function(testResult){
        test_signClientFileQP(testResult,context_net,{
            type: 3,
            cuttingRide: {
                mode: "oneSide",
                position: "right",
            }
        },2,false)
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF客户端图片签批-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_signClientFileQP(testResult,context_net,{
            type: 3,
            cuttingRide: {
                mode: "opposite",
                position: "right",
            }
        },2,false)
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF客户端图片签批-签名域",
    asyn: true,
    fun: function(testResult){
        test_signClientFileQP(testResult,context_net,{
            type: 2,
            signField: {
                allSignField: true,
            }
        },2,false,"signField.pdf")
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF客户端盖章-坐标-带时间戳",
    asyn: true,
    fun: function(testResult){
        test_signClientFile(testResult,context_net,{
            type: 0,
            pos: {
                page: "1",
                coordinate: "100,100"
            }
        },0,true)
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF客户端图片签批-坐标-带时间戳",
    asyn: true,
    fun: function(testResult){
        test_signClientFileQP(testResult,context_net,{
            type: 0,
            pos: {
                page: "1",
                coordinate: "100,100"
            }
        },2,true)
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF反向联合文字签批-坐标",
    asyn: true,
    fun: function(testResult){
        test_pdfSignSealHashFx(testResult,context_net,{
            location:{
                type: 0,
                pos: {
                    page: "1",
                    coordinate: "100,100"
                }
            }
        },1,false)
    }
});


fileSealNetTestSuit.AddTest({
    name: "PDF反向联合文字签批-关键字",
    asyn: true,
    fun: function(testResult){
        test_pdfSignSealHashFx(testResult,context_net,{
            location:{
                type: 1,
                keyword: {
                    page: "1",
                    keyword: "Hello",
                    offset: "0,0"
                }
            }
        },1,false)
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF反向联合文字签批-骑缝",
    asyn: true,
    fun: function(testResult){
        test_pdfSignSealHashFx(testResult,context_net,{
            location:{
                type: 3,
                cuttingRide: {
                    mode: "oneSide",
                    position: "right",
                }
            }
        },1,false)
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF反向联合文字签批-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_pdfSignSealHashFx(testResult,context_net,{
            location:{
                type: 3,
                cuttingRide: {
                    mode: "opposite",
                    position: "right",
                }
            }
        },1,false)
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF反向联合文字签批-签名域",
    asyn: true,
    fun: function(testResult){
        test_pdfSignSealHashFx(testResult,context_net,{
            location:{
                type: 2,
                signField: {
                    allSignField: true,
                }
            }
        },1,false,"signField.pdf")
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF反向联合图片签批-坐标",
    asyn: true,
    fun: function(testResult){
        test_pdfSignSealHashFx(testResult,context_net,{
            location:{
                type: 0,
                pos: {
                    page: "1",
                    coordinate: "100,100"
                }
            }
        },2,false)
    }
});


fileSealNetTestSuit.AddTest({
    name: "PDF反向联合图片签批-关键字",
    asyn: true,
    fun: function(testResult){
        test_pdfSignSealHashFx(testResult,context_net,{
            location:{
                type: 1,
                keyword: {
                    page: "1",
                    keyword: "Hello",
                    offset: "0,0"
                }
            }
        },2,false)
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF反向联合图片签批-骑缝",
    asyn: true,
    fun: function(testResult){
        test_pdfSignSealHashFx(testResult,context_net,{
            location:{
                type: 3,
                cuttingRide: {
                    mode: "oneSide",
                    position: "right",
                }
            }
        },2,false)
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF反向联合图片签批-双开骑缝",
    asyn: true,
    fun: function(testResult){
        test_pdfSignSealHashFx(testResult,context_net,{
            location:{
                type: 3,
                cuttingRide: {
                    mode: "opposite",
                    position: "right",
                }
            }
        },2,false)
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF反向联合图片签批-签名域",
    asyn: true,
    fun: function(testResult){
        test_pdfSignSealHashFx(testResult,context_net,{
            location:{
                type: 2,
                signField: {
                    allSignField: true,
                }
            }
        },2,false,"signField.pdf")
    }
});

fileSealNetTestSuit.AddTest({
    name: "PDF反向联合图片签批-坐标-带时间戳",
    asyn: true,
    fun: function(testResult){
        test_pdfSignSealHashFx(testResult,context_net,{
            location:{
                type: 0,
                pos: {
                    page: "1",
                    coordinate: "100,100"
                }
            }
        },2,true)
    }
});


allTestes.AddTestSuit(fileSealNetTestSuit);