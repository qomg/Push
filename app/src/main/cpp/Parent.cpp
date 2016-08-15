//
// Created by yazha on 2016-08-10.
//
#include "Parent.h"

/**
* 全局变量，代表应用程序进程.
*/
ProcessBase *m_process = NULL;
/**
    * 全局的JNIEnv，子进程有时会用到它.
    */
static JNIEnv* m_env = NULL;

Parent::Parent(JNIEnv *env, jobject jobj)
{
    LOGE("<<new parent instance>>");

    m_jobj = env->NewGlobalRef(jobj);
    m_env = env;
    m_process = this;
}

Parent::~Parent()
{
    LOGE( "<<Parent::~Parent()>>" );

    m_process = NULL;
}

void Parent::do_work()
{
}

JNIEnv* Parent::get_jni_env() const
{
    return m_env;
}

jobject Parent::get_jobj() const
{
    return m_jobj;
}

/**
* 父进程创建通道,这里其实是创建一个客户端并尝试
* 连接服务器(子进程)
*/
bool Parent::create_channel()
{
    int sockfd;

    sockaddr_un addr;

    while( 1 )
    {
        sockfd = socket( AF_LOCAL, SOCK_STREAM, 0 );

        if( sockfd < 0 )
        {
            LOGE("<<Parent create channel failed>>");

            return false;
        }

        memset(&addr, 0, sizeof(addr));

        addr.sun_family = AF_LOCAL;

        strcpy( addr.sun_path, path );

        if( connect( sockfd, (sockaddr*)&addr, sizeof(addr)) < 0 )
        {
            close(sockfd);

            sleep(1);

            continue;
        }

        set_channel(sockfd);

        LOGE("<<parent channel fd %d>>", m_channel );

        break;
    }

    return true;
}

/**
* 子进程死亡会发出SIGCHLD信号,通过捕捉此信号父进程可以
* 知道子进程已经死亡,此函数即为SIGCHLD信号的处理函数.
*/
static void sig_handler( int signo )
{
    pid_t pid;

    int status;

//调用wait等待子进程死亡时发出的SIGCHLD
//信号以给子进程收尸，防止它变成僵尸进程
    pid = wait(&status);

    if( m_process != NULL )
    {
        m_process->on_child_end();
    }
}

void Parent::catch_child_dead_signal()
{
    LOGE("<<process %d install child dead signal detector!>>", getpid());

    struct sigaction sa;

    sigemptyset(&sa.sa_mask);

    sa.sa_flags = 0;

    sa.sa_handler = sig_handler;

    sigaction( SIGCHLD, &sa, NULL );
}

void Parent::on_child_end()
{
    LOGE("<<on_child_end:create a new child process>>");

    create_child();
}

bool Parent::create_child( )
{
    pid_t pid;

    if( (pid = fork()) < 0 )
    {
        return false;
    }
    else if( pid == 0 ) //子进程
    {
        LOGE("<<In child process,pid=%d>>", getpid() );

        Child child;

        ProcessBase& ref_child = child;

        ref_child.do_work();
    }
    else if( pid > 0 )  //父进程
    {
        LOGE("<<In parent process,pid=%d>>", getpid() );
    }

    return true;
}