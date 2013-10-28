#pragma include includes/StdLib.frag
#define GAMMA
#pragma include includes/misc.h
#pragma include includes/gamma.h

#define lambert_reflectance_VAL 1
//#pragma brdf lambert

in vec3 normal;
in vec3 eye;
in vec3 lightDir;
in vec2 uv;

const vec3 COLOR1 = RGBColor(255, 184, 107);
const vec3 COLOR2 = RGBColor(107, 159, 255);

const vec3 SpecularColor = vec3(0.028);//vec3(1  ,         0.765557  ,  0.336057);//Gold
const float gloss = 0.3;
const float SpecularPower = exp2(10 * gloss + 1);
const float NORM_FACTOR = ((SpecularPower + 2) / 8 );

uniform sampler2D diffuse;

float saturate(float a)
{
    return min(1,max(0,a));
}

#define OneOnLN2_x6 8.656170 // == 1/ln(2) * 6   (6 is SpecularPower of 5 + 1)
vec3 FresnelSchlick(vec3 SpecularColor, vec3 L, vec3 H)
{
    return SpecularColor + (1.0f - SpecularColor) * exp2(-OneOnLN2_x6 * saturate(dot(L, H)));
}

vec3 lighting(vec3 L, vec3 N, vec3 V, vec3 diff, vec3 lColor){
    vec3 H = normalize(L+V);
    float NdotL = dot(L, N);

    vec3 f_schlick = FresnelSchlick(SpecularColor, L, H);
    float d_blinnPhong = pow(saturate(dot(N, H)), SpecularPower);

    vec3 spec = f_schlick * d_blinnPhong * NORM_FACTOR * NdotL;

    return (diff+spec) * lColor * max(0, NdotL);
}

void main()
{   
    vec3 N = normalize(normal);
    vec3 V = normalize(eye);
    vec3 diff = toLinear(texture(diffuse, uv)).rgb;

    vec3 L1 = normalize(lightDir);
    vec3 color =  lighting(L1, N, V, diff, COLOR1);

    vec3 L2 = L1;
    L2.xy = -L2.xy;
    color +=  lighting(L2, N, V, diff, COLOR2);

    FragColor = toGamma(vec4(color, 1));
}
