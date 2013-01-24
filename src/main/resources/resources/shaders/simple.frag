#version 120
#pragma include includes/StdLib.frag
#pragma include includes/misc.h
#define GAMMA
#pragma include includes/gamma.h

in vec3 normal;  
in vec3 eye;

const vec3 L = vec3(0.12708472893924555, -2.5399460893115735, 2.8320502943377743);

const vec3 COLOR1 = HexColor(0xFFB86B) * 0.5;
const vec3 COLOR2 = HexColor(0x6B9FFF) * 0.5;

//uniform mat2x4 dual;

vec3 light(vec3 N, vec3 L, vec3 color)
{
    float NdotL = dot(N, normalize(L));
    if(NdotL > 0)
    {
        vec3 d = color * NdotL;
        vec3 halfVector = normalize(L + eye);
        float HdotN = dot(N, halfVector);
        vec3 s = 0.4 * vec3(1, 0.8, 0.6) * pow(max(HdotN, 0.), 0.9);
        return d + s;
    }
    else
        return vec3(0);
}

void main()
{   
    vec3 N = normalize(normal);
    
    vec3 color = vec3(0.);
    color += light(N, L, COLOR2);
    color += light(N, reflect(L, vec3(-1, 1, 0)), COLOR1);

    FragColor = pow(vec4(color, 1), vec4(invgamma));
    //FragColor = abs(dual[0]);
}
