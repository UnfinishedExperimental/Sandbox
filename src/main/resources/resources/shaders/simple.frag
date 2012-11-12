#version 120
#pragma include includes/StdLib.frag
#pragma include includes/misc.h

in vec2 texcoord; 

uniform sampler2D data;

void main()
{   
    vec4 data = texture(data, texcoord);

    vec3 pos = data.xyz;
    float life = data.a;


    FragColor = vec4(life, pos);
}
