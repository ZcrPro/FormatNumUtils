package com.zcrpro.formatnumutils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.TextView;

import com.zcrpro.formatnum.BankInfoUtil;
import com.zcrpro.formatnum.ContentWithSpaceEditText;

public class MainActivity extends AppCompatActivity {


    private BankInfoUtil mInfoUtil;
    private TextView tvBankType;
    private TextView tvBankName;
    private TextView tvBankId;
    private ContentWithSpaceEditText tvAccountNo;
    private ContentWithSpaceEditText tvCardNo;
    private ContentWithSpaceEditText tvPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBankType = findViewById(R.id.tv_bank_type);
        tvBankName = findViewById(R.id.tv_bank_name);
        tvBankId = findViewById(R.id.tv_bank_id);
        tvAccountNo = findViewById(R.id.tv_account_no);
        tvCardNo = findViewById(R.id.bank_card_no);
        tvPhone = findViewById(R.id.tv_phone);

        tvAccountNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //自动检测输入的银行卡号信息
                checkBankInfo();
            }
        });

    }

    private void checkBankInfo() {
        String cardNum = tvAccountNo.getText().toString().replace(" ", "");
        if (!TextUtils.isEmpty(cardNum) && checkBankCard(cardNum)) {
            mInfoUtil = new BankInfoUtil(cardNum);
            tvBankName.setText(mInfoUtil.getBankName());
            tvBankId.setText(mInfoUtil.getBankId());
            tvBankType.setText(mInfoUtil.getCardType());
        } else {
            tvBankType.setText("");
            tvBankType.setHint("输入卡号自动匹配");
            tvBankName.setText("");
            tvBankName.setHint("输入卡号自动匹配");
            tvBankId.setText("");
            tvBankId.setHint("输入卡号自动匹配");
        }
    }

    /**
     * 校验过程：
     * 1、从卡号最后一位数字开始，逆向将奇数位(1、3、5等等)相加。
     * 2、从卡号最后一位数字开始，逆向将偶数位数字，先乘以2（如果乘积为两位数，将个位十位数字相加，即将其减去9），再求和。
     * 3、将奇数位总和加上偶数位总和，结果应该可以被10整除。
     * 校验银行卡卡号
     */
    public static boolean checkBankCard(String bankCard) {
        if (bankCard.length() < 15 || bankCard.length() > 19) {
            return false;
        }
        char bit = getBankCardCheckCode(bankCard.substring(0, bankCard.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return bankCard.charAt(bankCard.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhn 校验算法获得校验位
     */
    public static char getBankCardCheckCode(String nonCheckCodeBankCard) {
        if (nonCheckCodeBankCard == null || nonCheckCodeBankCard.trim().length() == 0
                || !nonCheckCodeBankCard.matches("\\d+")) {
            //如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeBankCard.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }
}
