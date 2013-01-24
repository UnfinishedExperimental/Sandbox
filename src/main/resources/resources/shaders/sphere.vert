#version 120
#pragma include includes/StdLib.vert

in vec3 in_position;    //<Position>

uniform mat4 view; //<MAT_V>
uniform mat4 projection; //<MAT_P>
uniform float time;

const float timeScale = 3;
const float PI = 3.14159265;

void main()
{
    vec3 p = in_position;
    //float theta = acos(p.z+1);
    float phi = atan(abs(p.y/p.x));
    float phi2 = atan(abs(p.z/p.x));

    //p *= sin(phi*5)*0.1+1;
    //p *= sin(phi2*10)*0.1+1;

    float t = mod(time,timeScale)/timeScale;
    //t = abs(t*2-1);

    float dist = 1 - abs(p.y - (t - 0.5)*2);
    //p *= 1 + sin(phi*(20*t+2))*0.1;//+ pow(dist,5)*0.3;
    //p.y += sin(p.y*10+time*5)*0.3;
    //p.x += sin(p.x*8+time)*0.3;
    gl_Position =  projection * vec4(p, 1.);
}