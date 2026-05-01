var ofdOtherTestSuit = new TestSuit("OFD签章其它<br>");

ofdOtherTestSuit.AddTest({
    name: "创建签章环境",
    asyn: true,
    fun: function(testResult){
        test_createContext(testResult,context_stand_alone)
    }
});

ofdOtherTestSuit.AddTest({
    name: "创建文件签章环境",
    asyn: true,
    fun: function(testResult){
        test_createFileSealContext(testResult,context_stand_alone)
    }
});

ofdOtherTestSuit.AddTest({
    name: "OFD转PDF",
    asyn: true,
    fun: function(testResult){
        var name=context_stand_alone
        var ofdFileSrc = fileRoot+pathJoin("files","1.ofd")
        var pdfFileDest = fileRoot+pathJoin("files","2.pdf")
        fileSealContext[name].ofd2pdf({
            inFile: ofdFileSrc,
            outFile:pdfFileDest,
            success:function (){
                testResult.done()
            },
            fail:function (e){
                testResult.fail(e.message)
            }
        })
    }
});

ofdOtherTestSuit.AddTest({
    name: "PDF转OFD",
    asyn: true,
    fun: function(testResult){
        var name=context_stand_alone
        var pfdFileSrc = fileRoot+pathJoin("files","1.pdf")
        var ofdFileDest = fileRoot+pathJoin("files","2.ofd")
        fileSealContext[name].pdf2ofd({
            inFile: pfdFileSrc,
            outFile:ofdFileDest,
            success:function (){
                testResult.done()
            },
            fail:function (e){
                testResult.fail(e.message)
            }
        })
    }
});

allTestes.AddTestSuit(ofdOtherTestSuit);