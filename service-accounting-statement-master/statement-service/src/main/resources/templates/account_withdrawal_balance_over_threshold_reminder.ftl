<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Document</title>
</head>
<body>
<div
        style="
        color: #1f2329;
        font-family: LarkEmojiFont, LarkChineseQuote, -apple-system, system-ui,
          'Helvetica Neue', Tahoma, 'PingFang SC', 'Microsoft Yahei', Arial,
          'Hiragino Sans GB', sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji',
          'Segoe UI Symbol', 'Noto Color Emoji';
        font-size: 16px;
        font-weight: 400;
        background-color: rgb(248, 248, 248);
        width: 100%;
        padding-top: 40px;
        line-height: 25px;
      "
>
    <div
            style="
          padding: 20px 20px 40px 20px;
          background-color: #fff;
          text-align: left;
        "
    >
        <div style="margin: 0px auto 20px; text-align: center">
            <img
                    width="138"
                    src="https://accounting-service-daily-bill-prod.s3.ap-southeast-1.amazonaws.com/web/logo.png"
                    style="
              box-sizing: border-box;
              border: 0px;
              display: inline-block;
              outline: none;
              text-decoration: none;
              height: auto;
              max-width: 100%;
              padding: 0px;
            "
            />
        </div>
        <div
                style="
            border: 1px solid gray;
            border-left: none;
            border-right: none;
            height: 1px;
          "
        ></div>
        <div
                style="
            font-size: 26px;
            font-weight: bold;
            margin: 60px auto;
            color: rgb(53, 152, 219);
            text-align: center;
          "
        >
            Withdrawal Balance Over Threshold Reminder
        </div>
        <div style="text-align: center;">
            <table style="width: 35%; margin: 10px auto; padding: 14px 10px; background-color: #FFFFFF;
                border-collapse: collapse; font-size: 13px; border: 1px solid #D9D9D9; text-align: center;
                white-space: nowrap;">
                <tr style="color: #000000; ">
                    <th style="width: 120px; border: 1px solid #D9D9D9; padding: 2px;text-align: left;"> Merchant</th>
                    <th style="min-width: 120px; border: 1px solid #D9D9D9;text-align: left;">${merchantName}</th>
                </tr>
                <tr style="color: #000000; ">
                    <th style="width: 120px; border: 1px solid #D9D9D9; padding: 2px;text-align: left;"> Country</th>
                    <th style="min-width: 120px; border: 1px solid #D9D9D9;text-align: left;">${country}</th>
                </tr>
                <tr style="color: #000000; ">
                    <th style="width: 120px; border: 1px solid #D9D9D9; padding: 2px;text-align: left;">
                        TransactionType
                    </th>
                    <th style="min-width: 120px; border: 1px solid #D9D9D9;text-align: left;">${transactionType}</th>
                </tr>
                <tr style="color: #000000; ">
                    <th style="width: 120px; border: 1px solid #D9D9D9; padding: 2px;text-align: left;"> Item</th>
                    <th style="min-width: 120px; border: 1px solid #D9D9D9;text-align: left;">Withdrawable balance</th>
                </tr>
                <tr style="color: #000000; ">
                    <th style="width: 120px; border: 1px solid #D9D9D9; padding: 2px;text-align: left;"> Threshold</th>
                    <th style="min-width: 120px; border: 1px solid #D9D9D9;text-align: left;">
                        ${thresholdAmount}(${currency})
                    </th>
                </tr>
                <tr style="color: #000000; ">
                    <th style="width: 160px; border: 1px solid #D9D9D9; padding: 2px;text-align: left;"> Withdrawable
                        Balance
                    </th>
                    <th style="min-width: 120px; border: 1px solid #D9D9D9;text-align: left;">
                        ${withdrawalAmount}(${currency})
                    </th>
                </tr>
                <tr style="color: #000000; ">
                    <th style="width: 120px; border: 1px solid #D9D9D9; padding: 2px;text-align: left;"> TimeStamp</th>
                    <th style="min-width: 120px; border: 1px solid #D9D9D9;text-align: left;">${timeStamp}</th>
                </tr>
            </table>
        </div>
    </div>
    <!-- </div> -->
</div>
</body>
</html>
