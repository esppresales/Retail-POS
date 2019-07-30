<?php

namespace app\models;

use Yii;

/**
 * This is the model class for table "discounts".
 *
 * @property integer $id
 * @property string $percentage
 * @property string $from_date
 * @property string $to_date
 * @property string $min_restriction
 * @property string $min_spend
 * @property string $created_date
 * @property string $modified_date
 */
class Discounts extends \yii\db\ActiveRecord
{
    /**
     * @inheritdoc
     */
    public static function tableName()
    {
        return 'discounts';
    }

    /**
     * @inheritdoc
     */
    public function rules()
    {
        return [
            [['percentage', 'from_date', 'to_date', 'min_restriction', 'min_spend', 'created_date'], 'required'],
            [['percentage', 'min_spend'], 'number'],
            [['from_date', 'to_date', 'created_date', 'modified_date'], 'safe'],
            [['min_restriction'], 'string']
        ];
    }

    /**
     * @inheritdoc
     */
    public function attributeLabels()
    {
        return [
            'id' => 'ID',
            'percentage' => 'Percentage',
            'from_date' => 'From Date',
            'to_date' => 'To Date',
            'min_restriction' => 'Min Restriction',
            'min_spend' => 'Min Spend',
            'created_date' => 'Created Date',
            'modified_date' => 'Modified Date',
        ];
    }
}
