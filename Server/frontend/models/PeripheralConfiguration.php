<?php

namespace app\models;

use Yii;

/**
 * This is the model class for table "peripheral_configuration".
 *
 * @property integer $id
 * @property string $barcode_device_name
 * @property string $msr_device_name
 * @property string $created_date
 * @property string $modified_date
 */
class PeripheralConfiguration extends \yii\db\ActiveRecord
{
    /**
     * @inheritdoc
     */
    public static function tableName()
    {
        return 'peripheral_configuration';
    }

    /**
     * @inheritdoc
     */
    public function rules()
    {
        return [
            [['created_date', 'modified_date'], 'required'],
            [['created_date', 'modified_date'], 'safe'],
            [['barcode_device_name', 'msr_device_name'], 'string', 'max' => 40]
        ];
    }

    /**
     * @inheritdoc
     */
    public function attributeLabels()
    {
        return [
            'id' => 'ID',
            'barcode_device_name' => 'Barcode Device Name',
            'msr_device_name' => 'Msr Device Name',
            'created_date' => 'Created Date',
            'modified_date' => 'Modified Date',
        ];
    }
}
