#version 120
#pragma include includes/StdLib.frag
#pragma include includes/misc.h

in vec2 texcoord; 

uniform sampler2D data;

void main()
{
    vec4 data = texture(data, texcoord);

    vec3 pos = data.xyz*2-1;
    float life = data.a;
   
    life -= 0.005;

    vec2 dir = pos.yx;
    dir = normalize(dir) * 0.04;
    dir.y *= -1.;
    pos.xy += dir;

    if(life < 0.){
        pos = vec3(rand(texcoord), rand(texcoord.ts), 0)*2-1;
        life = 1;
    }
  
    FragColor = vec4(pos*0.5+0.5, life);
}
