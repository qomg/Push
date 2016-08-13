//
// Created by yazha on 2016-08-10.
//

#ifndef PUSH_UTILS_H
#define PUSH_UTILS_H

#include <jni.h>
#include <string>
#include <sys/system_properties.h>

/* c/c++ string turn to java jstring */
static jstring strTojstring(JNIEnv* env, const unsigned char* pStr)
{
    int        strLen    = strlen((const char*)pStr);
    jclass     jstrObj   = env->FindClass("java/lang/String");
    jmethodID  methodId  = env->GetMethodID(jstrObj, "", "([BLjava/lang/String;)V");
    jbyteArray byteArray = env->NewByteArray(strLen);
    jstring    encode    = env->NewStringUTF("utf-8");
    env->SetByteArrayRegion(byteArray, 0, strLen, (jbyte*)pStr);
    return (jstring)env->NewObject(jstrObj, methodId, byteArray, encode);
}

/* java jstring turn to c/c++ string */
static char* jstringTostr(JNIEnv* env, jstring jstr)
{
    char* pStr = NULL;
    jclass     jstrObj   = env->FindClass("java/lang/String");
    jstring    encode    = env->NewStringUTF("utf-8");
    jmethodID  methodId  = env->GetMethodID(jstrObj, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray byteArray = (jbyteArray)env->CallObjectMethod(jstr, methodId, encode);
    jsize      strLen    = env->GetArrayLength(byteArray);
    jbyte      *jBuf     = env->GetByteArrayElements(byteArray, JNI_FALSE);

    if (jBuf > 0)
    {
        pStr = (char*)malloc(strLen + 1);
        if (!pStr)
        {
            return NULL;
        }
        memcpy(pStr, jBuf, strLen);
        pStr[strLen] = 0;
    }
    env->ReleaseByteArrayElements(byteArray, jBuf, 0);
    return pStr;
}

/**
 *  get the android version code
 */
int get_version(){
    char value[8] = "";
    __system_property_get("ro.build.version.sdk", value);
    return atoi(value);
}

#endif //PUSH_UTILS_H
