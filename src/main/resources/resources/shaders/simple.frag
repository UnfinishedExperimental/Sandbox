#pragma include includes/StdLib.frag
#pragma include includes/misc.h
#define GAMMA
#pragma include includes/gamma.h

in vec3 normal;  
in vec3 eye;

const vec3 COLOR1 = RGBColor(255, 184, 107);
const vec3 COLOR2 = RGBColor(107, 159, 255);

const vec3 specColor = vec3(0.0225);
const float gloss = 0.9;
const float SpecularPower = exp2(10 * gloss + 1);
const float normFactor = ((SpecularPower + 2) / 8 );

const vec3 directDiffuse = vec3(0.5);

//uniform mat2x4 dual;

float saturate(float a)
{
    return min(1,max(0,a));
}

#define OneOnLN2_x6 8.656170 // == 1/ln(2) * 6   (6 is SpecularPower of 5 + 1)
vec3 FresnelSchlick(vec3 E,vec3 H)
{
    return specColor + (1.0f - specColor) * exp2(-OneOnLN2_x6 * saturate(dot(E, H)));
}

float BlinnPhong(vec3 N, vec3 H)
{
    return pow(saturate(dot(N, H)), SpecularPower);
}

vec3 light(vec3 N, vec3 V, vec3 L, vec3 lightColor)
{
    vec3 H = normalize(L+V);
    float NdotL = dot(N, L);

    vec3 directSpecular = FresnelSchlick(L, H) * BlinnPhong(N, H) * normFactor;
    return (directDiffuse + directSpecular) * lightColor * max(0,NdotL);
}

void main()
{   
    vec3 N = normalize(normal);
    vec3 V = normalize(eye);
    vec3 L = normalize(vec3(0.12708472893924555, -2.5399460893115735, 2.8320502943377743));


    vec3 color = light(N, V, L, COLOR1);

    L = reflect(L, vec3(-1, 1, 0));
    color += light(N, V, L, COLOR2);

    FragColor = toGamma(vec4(color, 1));
}
