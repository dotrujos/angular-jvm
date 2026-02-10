#include <jni.h>
#include "../headers/com_gabrielaraujo_angular_Main.h"
#include <iostream>
#include <string>
#include <format>

std::string ReplaceAll(std::string str, const std::string& from, const std::string& to) {
    size_t start_pos = 0;
    while((start_pos = str.find(from, start_pos)) != std::string::npos) {
        str.replace(start_pos, from.length(), to);
        start_pos += to.length(); // Handles case where 'to' is a substring of 'from'
    }
    return str;
}

JNIEXPORT jstring JNICALL Java_com_gabrielaraujo_angular_Main_replaceVariableOcurrencies
  (JNIEnv *env, jobject thisObj, jstring html, jobjectArray keys, jobjectArray values) {
	int keysLen = env->GetArrayLength(keys);
	int valuesLen = env->GetArrayLength(values);

	if (html == NULL) {
		return html;
	}

	if ((keysLen == 0 || valuesLen == 0) || (keysLen != valuesLen)) {
		return html;
	}

	std::string result = env->GetStringUTFChars(html, NULL);

	for (int i = 0; i < keysLen; i++) {
		jstring keyString = (jstring) env->GetObjectArrayElement(keys, i);
		jstring valueString = (jstring) env->GetObjectArrayElement(values, i);

		const char* k = env->GetStringUTFChars(keyString, NULL);
		const char* v = env->GetStringUTFChars(valueString, NULL);

		{
			std::string placeholder = "{{ " + std::string(k) + " }}";
			result = ReplaceAll(result, placeholder, v);
		}

		env->ReleaseStringUTFChars(keyString, k);
		env->ReleaseStringUTFChars(valueString, v);
	}

	return env->NewStringUTF(result.c_str());
}
