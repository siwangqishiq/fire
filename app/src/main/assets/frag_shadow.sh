#version 300 es
precision highp float;
in vec4 vPosition;  //���մӶ�����ɫ�������Ĳ���
out float fragColor;//�������ƬԪ��ɫ
uniform highp vec3 uLightLocation;	//��Դλ��
void main()
{
   float dis=distance(vPosition.xyz,uLightLocation);
   fragColor=dis;
}