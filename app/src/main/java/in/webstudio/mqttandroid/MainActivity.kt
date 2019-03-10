package `in`.webstudio.mqttandroid

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    val ipaddress = "tcp://192.168.0.107"
    val userName = "sbjdjlog"
    val password = "N36nroRb4OIn"
    val port = 1883

    val persistence = MemoryPersistence()
    val options = MqttConnectOptions()

    val tempTopic = "temperature"
    val dangerTopic = "danger"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mqttClient = MqttAndroidClient(applicationContext,"$ipaddress:$port", MqttClient.generateClientId())
//
//        options.userName = userName
//        options.password = password.toCharArray()

        connect_mqtt.setOnClickListener {
            connectToServer(mqttClient)
        }

    }


    fun connectToServer(mqttClient:MqttAndroidClient){
        try{
            Log.d(TAG,"Trying to Connect")
            val connection = mqttClient.connect(options)

            connection.actionCallback = object : IMqttActionListener{
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG,"Connected Successfully")
                    Toast.makeText(applicationContext,"Connected",Toast.LENGTH_LONG).show()

                    mqttClient.subscribe(tempTopic,1)
                    mqttClient.subscribe(dangerTopic,1)
                    mqttClient.setCallback(object : MqttCallback{
                        override fun messageArrived(topic: String?, message: MqttMessage?) {
                            Log.d(TAG,"message ${message!!.payload}")
                            if(topic==tempTopic)
                            {
                                temp_text.text = "${String(message.payload)}"
                            }
                            else if(topic==dangerTopic)
                            {
                                danger_text.text = "${String(message!!.payload)}"
                            }
                        }

                        override fun connectionLost(cause: Throwable?) {
                            Log.d(TAG,"Connection Lost")
                        }

                        override fun deliveryComplete(token: IMqttDeliveryToken?) {
                            Log.d(TAG,"Message Sent")
                        }
                    })
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG,"Connection Failed with exception ${exception!!.printStackTrace()} ")
                    Toast.makeText(applicationContext,"Failed",Toast.LENGTH_LONG).show()
                }
            }
        }catch (exception:MqttException){
            exception.printStackTrace()
        }
    }
}
