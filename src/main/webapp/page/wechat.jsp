<%--
  Created by IntelliJ IDEA.
  User: HanleyTang
  Date: 2018-12-02
  Time: 17:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>微信支付</title>
    <link rel="stylesheet" href="https://cdn.bootcss.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
    <script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
    <script src="https://cdn.bootcss.com/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
    <script src="https://cdn.bootcss.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
    <style>
        body{
            padding: 20px;
        }
    </style>

    <script type="text/javascript">
        function action(type) {
            switch (type) {
                case 1:
                    var amt1 = $("#amt1").val();
                    var barcode = $("#barcode").val();
                    window.location.href="/wechat/pay/micro_pay?amt="+amt1+"&barcode="+barcode;
                    break;
                case 2:
                    var amt2 = $("#amt2").val();
                    window.location.href="/wechat/pay/qrcode?amt="+amt2;
                    break;
                case 3:
                    var amt3 = $("#amt2").val();
                    window.location.href="/wechat/pay/mp?amt="+amt3;
                    break;
                case 4:
                    var amt = $("#amt").val();
                    var outTradeNo = $("#out_trade_no").val();
                    window.location.href="/wechat/refund?amt="+amt+"&out_trade_no="+outTradeNo;
                    break;
            }
        }
    </script>
</head>
<body>

<div>

    <div class="alert alert-primary text-center" role="alert">
        微信支付 示例
    </div>

    <div class="card  mb-3">
        <div class="card-header">
            刷卡支付
        </div>
        <div class="card-body">
            <input placeholder="输入支付码" id="barcode"  />
            <input placeholder="输入金额" id="amt1" value="0.01" readonly  /><button onclick="action(1)"> 刷卡支付</button>
        </div>
    </div>

    <div class="card  mb-3">
        <div class="card-header">
            扫码支付
        </div>
        <div class="card-body">
            <input placeholder="输入金额" id="amt2" value="0.01" readonly  /><button onclick="action(2)">扫码支付</button>
        </div>
    </div>

    <div class="card  mb-3">
        <div class="card-header">
            公众号支付
        </div>
        <div class="card-body">
            <input placeholder="输入金额" id="amt3" value="0.01" readonly  /><button onclick="action(3)">公众号支付</button>
        </div>
    </div>


    <div class="card  mb-3">
        <div class="card-header">
            支付订单退款
        </div>
        <div class="card-body">
            <input placeholder="外部单号(out_trade_no)" id="out_trade_no" value="" />
            <input placeholder="输入金额" id="amt" value="0.01" readonly /><button onclick="action(4)">开始退款</button>
        </div>
    </div>

</div>


</body>
</html>
