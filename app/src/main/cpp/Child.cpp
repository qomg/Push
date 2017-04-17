//
// Created by yazha on 2016-08-10.
//
#include "Child.h"


bool Child::create_child( )
{
//子进程不需要再去创建子进程,此函数留空
    return false;
}

Child::Child()
{
    RTN_MAP.member_rtn = &Child::parent_monitor;
}

Child::~Child()
{
    LOGE("<<~Child(), unlink %s>>", path);

    unlink(path);
}

void Child::catch_child_dead_signal()
{
//子进程不需要捕捉SIGCHLD信号
    return;
}

void Child::on_child_end()
{
//子进程不需要处理
    return;
}

void Child::handle_parent_die( )
{
//子进程成为了孤儿进程,等待被Init进程收养后在进行后续处理
    while( getppid() != 1 )
    {
        usleep(500); //休眠0.5ms
    }

    close( m_channel );

//重启父进程服务
    LOGE( "<<parent died,restart now>>" );

    restart_parent();
}

void Child::restart_parent()
{
    LOGE("<<restart_parent enter>>");

/**
* TODO 重启父进程,通过am启动Java空间的任一组件(service或者activity等)即可让应用重新启动
*/
//    int version = get_sdk_version();
//    if (version >= 17 || version == 0) {
        execlp("am", "am", "startservice", "--user", g_userId, "-n", service_name, (char *) NULL);
//    } else {
//        execlp("am", "am", "startservice", "-n", service_name, (char *) NULL);
//    }
}

void* Child::parent_monitor()
{
    handle_parent_die();
}

void Child::start_parent_monitor()
{
    pthread_t tid;

    pthread_create( &tid, NULL, RTN_MAP.thread_rtn, this );
}

bool Child::create_channel()
{
    int listenfd, connfd;

    struct sockaddr_un addr;

    listenfd = socket( AF_LOCAL, SOCK_STREAM, 0 );

    unlink(path);

    memset( &addr, 0, sizeof(addr) );

    addr.sun_family = AF_LOCAL;

    strcpy( addr.sun_path, path );

    if( bind( listenfd, (sockaddr*)&addr, sizeof(addr) ) < 0 )
    {
        LOGE("<<bind error,errno(%d)>>", errno);

        return false;
    }

    listen( listenfd, 5 );

    while( true )
    {
        if( (connfd = accept(listenfd, NULL, NULL)) < 0 )
        {
            if( errno == EINTR)
                continue;
            else
            {
                LOGE("<<accept error>>");

                return false;
            }
        }

        set_channel(connfd);

        break;
    }

    LOGE("<<child channel fd %d>>", m_channel );

    return true;
}

void Child::handle_msg( const char* msg )
{
//TODO How to handle message is decided by you.
}

void Child::listen_msg( )
{
    fd_set rfds;

    int retry = 0;

    while( 1 )
    {
        FD_ZERO(&rfds);

        FD_SET( m_channel, &rfds );

        timeval timeout = {3, 0};

        int r = select( m_channel + 1, &rfds, NULL, NULL, &timeout );

        if( r > 0 )
        {
            char pkg[256] = {0};

            if( FD_ISSET( m_channel, &rfds) )
            {
                read_from_channel( pkg, sizeof(pkg) );

                LOGE("<<A message comes:%s>>", pkg );

                handle_msg( (const char*)pkg );
            }
        }
    }
}

void Child::do_work()
{
    start_parent_monitor(); //启动监视线程

    if( create_channel() )  //等待并且处理来自父进程发送的消息
    {
        listen_msg();
    }
}
