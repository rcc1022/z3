import { service, illegal } from './http';
const sign = (success) => {
    service('/player/sign', {}, data => {
        if (data.ban === 1)
            illegal();
        else
            success(data);
    });
    // service('/player/sign', {}, data => {
    //     if (data.nick) {
    //         if (success)
    //             success(data);

    //         return;
    //     }

    //     service('/home/wx-sign-in-url', { host: location.host }, data => {
    //         location.href = data;
    //     });
    // });
}

const signOut = (success) => {
    service('/user/sign-out', {}, success);
}

export {
    sign,
    signOut
}