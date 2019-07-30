<?php

namespace app\models;

use Yii;

/**
 * This is the model class for table "printer_configuration".
 *
 * @property integer $id
 * @property string $master_ip
 * @property string $slave_ip
 * @property string $created_date
 * @property string $modified_date
 */
class PrinterConfiguration extends \yii\db\ActiveRecord
{
    /**
     * @inheritdoc
     */
    public static function tableName()
    {
        return 'printer_configuration';
    }

    /**
     * @inheritdoc
     */
    public function rules()
    {
        return [
            [['created_date'], 'required'],
            [['created_date', 'modified_date'], 'safe'],
            [['master_ip', 'slave_ip'], 'string', 'max' => 20]
        ];
    }

    /**
     * @inheritdoc
     */
    public function attributeLabels()
    {
        return [
            'id' => 'ID',
            'master_ip' => 'Master Ip',
            'slave_ip' => 'Slave Ip',
            'created_date' => 'Created Date',
            'modified_date' => 'Modified Date',
        ];
    }
}
