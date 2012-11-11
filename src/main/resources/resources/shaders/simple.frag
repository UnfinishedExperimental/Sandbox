#version 120
#pragma include includes/StdLib.frag

in vec2 texcoord; 

uniform vec3 lightPos = vec3(0,0,1);

const float BORDER_SIZE = 0.001;

float sdSphere(vec3 p, float s )
{
  return length(p)-s;
}

float sdTorus( vec3 p, vec2 t )
{
  vec2 q = vec2(length(p.xz)-t.x,p.y);
  return length(q)-t.y;
}

float sdTorus1(vec3 p)
{
    return sdTorus(p, vec2(1));
}

/*vec3 opTx( vec3 p, mat4 m )
{
    vec3 q = invert(m)*p;
    return primitive(q);
}

float opScale( vec3 p, float s )
{
    return primitive(p/s)*s;
}*/

const float eps = 0.001;
#define grad(func, p ) normalize(vec3(        func(p+vec3(eps,0,0)) - func(p-vec3(eps,0,0)), func(p+vec3(0,eps,0)) - func(p-vec3(0,eps,0)),  func(p+vec3(0,0,eps)) - func(p-vec3(0,0,eps))  ))

#define calcNormal(distance, point, bump, fbm) normalize( grad( distance, point) ) + bump*grad( fbm, point) ) )


void main()
{
    vec2 pos = texcoord * 2. - 1.;

float w = -3.14/4;
    float c = cos(w);
    float s = sin(w);

    mat3 m = mat3(vec3(1,0,0),
                  vec3(0,c,-s),
                  vec3(0,s,c));


vec3 pos3D = vec3(pos, 0);
int count = 100;
for(int i=0; i<count;++i)
{
    vec3 p3 = pos3D + vec3(0,0,-2);
    float dist = sdTorus1(p3/0.5)*0.5;
    if(dist > 0.)
    {
        pos3D.z += dist;
        if(i==count-1)
            discard;
    }
}
    
    vec3 normal = grad(sdTorus1, pos3D);

    float NdotL = dot(normalize(lightPos - pos3D), normal);

    //float alpha = len < 1. ? 1 : 1. - (len - 1.) / BORDER_SIZE;
    FragColor = vec4(NdotL + vec3(1,0.9,0.5),1);
}
