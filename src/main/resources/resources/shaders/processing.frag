#version 120
#pragma include includes/StdLib.frag
#pragma include includes/misc.h

in vec2 texcoord; 

uniform sampler2D data;

void main()
{
    vec3 color = Unpack3PNFromFP32(texture(data, texcoord).x) + 0.01;
    if(color.x > 1)
        color = vec3(1);
  
    FragColor = vec4(Pack3PNForFP32(color), vec3(0));
}
