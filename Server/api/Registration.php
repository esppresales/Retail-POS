<?php

namespace app\models;

use Yii;

/**
 * This is the model class for table "registration".
 *
 * @property integer $id
 * @property string $firstname
 * @property string $lastname
 * @property string $dob
 * @property string $email_id
 * @property string $address
 * @property string $username
 * @property string $password
 * @property string $gender
 * @property string $profession
 * @property string $up_image
 * @property string $images
 */
class Registration extends \yii\db\ActiveRecord
{

    /**
     * @inheritdoc
     */
	/**
	 * @return string the associated database table name
	 */
        public $cnf_password;
        public $verify;
        const WEAK=0;
        const STRONG=1;
	public function tableName()
	{
		return 'registration';
	}

	/**
	 * @return array validation rules for model attributes.
	 */
	public function rules()
	{
		// NOTE: you should only define rules for those attributes that
		// will receive user inputs.
		return array(
			array('firstname, lastname, username, password', 'required'),
			array('firstname, lastname, password', 'length', 'max'=>50),
			array('email_id, username, profession', 'length', 'max'=>255),
			array('address', 'length', 'max'=>1000),
			array('gender', 'length', 'max'=>6),
			array('dob', 'safe'),
                        array('username', 'unique'),
//                        array('password', 'passwordStrength','strength'=>self::STRONG),
                        array('email_id','email'),
//                        array('up_image', 'file', 'types'=>'jpg, gif, png, jpeg'),
//                        array('password', 'compare', 'compareAttribute'=>'cnf_password'),
//                        array('verify', 'captcha', 'allowEmpty'=>!CCaptcha::checkRequirements()),
//                        
			// The following rule is used by search().
			// @todo Please remove those attributes that should not be searched.
			array('id, firstname, lastname, dob, email_id, address, username, password, gender, profession', 'safe', 'on'=>'search'),
		);
	}

	/**
	 * @return array relational rules.
	 */
	public function relations()
	{
		// NOTE: you may need to adjust the relation name and the related
		// class name for the relations automatically generated below.
		return array(
		);
	}

	/**
	 * @return array customized attribute labels (name=>label)
	 */
	public function attributeLabels()
	{
		return array(
			'id' => 'ID',
			'firstname' => 'First Name',
			'lastname' => 'Lastname',
			'dob' => 'Dob',
			'email_id' => 'Email',
			'address' => 'Address',
			'username' => 'Username',
			'password' => 'Password',
                        'cnf_password' => 'Confirm Password',
			'gender' => 'Gender',
                        'up_image' => 'Upload Image',
                        'verify' => 'Verification Code',
                        'images' => 'Multiple Images',
		);
	}
        
        public static function enumItem($model,$attribute)
        {
                $attr=$attribute;
              //  self::resolveName($model,$attr);
                preg_match('/\((.*)\)/',$model->tableSchema->columns[$attr]->dbType,$matches);
                foreach(explode(',', $matches[1]) as $value)
                {
                        $value=str_replace("'",null,$value);
                        $values[$value]=Yii::t('enumItem',$value);
                }
                
                return $values;
        }
        
        public function passwordStrength($attribute,$params)
        {
            if ($params['strength'] === self::WEAK)
                $pattern = '/^(?=.*[a-zA-Z0-9]).{5,}$/';  
            elseif ($params['strength'] === self::STRONG)
                $pattern = '/^(?=.*\d(?=.*\d))(?=.*[a-zA-Z](?=.*[a-zA-Z])).{5,}$/';  

            if(!preg_match($pattern, $this->$attribute))
              $this->addError($attribute, 'your password is not strong enough!');
        }
        
        public function validatePassword($password,$username) {
            //$sql = "Select password from registration where username = $username";
            $sql = Yii::app()->db->createCommand("Select password from registration where username ='". $username."'")->queryAll();
            if($sql[0]['password'] == $password)
                return true;
            else 
                return false;
        }
        public function mailsend($to,$from,$subject,$message){
            $mail=Yii::app()->Smtpmail;
            $mail->SetFrom($from, 'From NAme');
            $mail->Subject    = $subject;
            $mail->MsgHTML($message);
            $mail->AddAddress($to, "");
            echo $mail;exit;
            if(!$mail->Send()) {
                echo "Mailer Error: " . $mail->ErrorInfo;
            }else {
                echo "Message sent!";
            }
        }

        /**
	 * Retrieves a list of models based on the current search/filter conditions.
	 *
	 * Typical usecase:
	 * - Initialize the model fields with values from filter form.
	 * - Execute this method to get CActiveDataProvider instance which will filter
	 * models according to data in model fields.
	 * - Pass data provider to CGridView, CListView or any similar widget.
	 *
	 * @return CActiveDataProvider the data provider that can return the models
	 * based on the search/filter conditions.
	 */
	public function search()
	{
		// @todo Please modify the following code to remove attributes that should not be searched.

		$criteria=new CDbCriteria;

		$criteria->compare('id',$this->id);
		$criteria->compare('firstname',$this->firstname,true);
		$criteria->compare('lastname',$this->lastname,true);
		$criteria->compare('dob',$this->dob,true);
		$criteria->compare('email_id',$this->email_id,true);
		$criteria->compare('address',$this->address,true);
		$criteria->compare('username',$this->username,true);
		$criteria->compare('password',$this->password,true);
		$criteria->compare('gender',$this->gender,true);
		$criteria->compare('profession',$this->profession,true);

		return new CActiveDataProvider($this, array(
                    
			'criteria'=>$criteria,
		));
	}

	/**
	 * Returns the static model of the specified AR class.
	 * Please note that you should have this exact method in all your CActiveRecord descendants!
	 * @param string $className active record class name.
	 * @return Registration the static model class
	 */
	public static function model($className=__CLASS__)
	{
		return parent::model($className);
	}
}
