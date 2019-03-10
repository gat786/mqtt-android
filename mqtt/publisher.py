import paho.mqtt.client as mqtt
import time

client  = mqtt.Client()

adress = "192.168.0.107"

port = 1883

client.connect(adress,port=port)

temp = input()
danger = input()

client.publish("temperature",payload=temp)
time.sleep(0.1)
client.publish("danger",payload=danger)
