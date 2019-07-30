<?php

namespace api\modules\v1\models;
use \yii\db\ActiveRecord;

/**
 * This is the model class for table "coupons".
 *
 * @property integer $id
 * @property string $coupon_code
 * @property string $amount
 * @property string $validity_from_date
 * @property string $validity_to_date
 * @property string $created_date
 * @property string $modified_date
 */
class Coupons extends ActiveRecord
{
    /**
     * @inheritdoc
     */
    public static function tableName()
    {
        return 'coupons';
    }

    /**
     * @inheritdoc
     */
    public function rules()
    {
        return [
            [['coupon_code', 'amount', 'validity_from_date', 'validity_to_date', 'created_date'], 'required'],
            [['amount'], 'number'],
            [['validity_from_date', 'validity_to_date', 'created_date', 'modified_date'], 'safe'],
            [['coupon_code'], 'string', 'max' => 20],
            [['coupon_code'], 'unique']
        ];
    }

    /**
     * @inheritdoc
     */
    public function attributeLabels()
    {
        return [
            'id' => 'ID',
            'coupon_code' => 'Coupon Code',
            'amount' => 'Amount',
            'validity_from_date' => 'Validity From Date',
            'validity_to_date' => 'Validity To Date',
            'created_date' => 'Created Date',
            'modified_date' => 'Modified Date',
        ];
    }
}
