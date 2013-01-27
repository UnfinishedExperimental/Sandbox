#pragma include includes/StdLib.frag
#define GAMMA
#pragma include includes/gamma.h

#define blinnphong_n_VAL 10
#pragma brdf blinnphong

in vec3 normal;
in vec3 eye;

void main()
{   
    vec3 N = normalize(normal);
    vec3 L = normalize(vec3(0,1,1));

    vec3 color = blinnphong(L, eye, N);

    FragColor = toGamma(vec4(color, 1));
}
