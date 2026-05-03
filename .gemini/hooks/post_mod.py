#!/usr/bin/env python3
import sys
import json
import subprocess
import os
import re

def get_module_name(file_path):
    """
    파일 경로에서 Gradle 모듈 이름을 추출합니다.
    """
    parts = file_path.split(os.sep)
    if not parts:
        return None
    
    if parts[0] == 'app':
        return ':app'
    
    if parts[0] in ['core', 'feature']:
        if len(parts) > 1 and parts[1] != 'src':
             return f':{parts[0]}:{parts[1]}'
        return f':{parts[0]}'
    
    if parts[0] in ['domain', 'data', 'build-logic']:
        return f':{parts[0]}'
        
    return None

def find_balanced_parenthesis(text, start_index):
    count = 0
    for i in range(start_index, len(text)):
        if text[i] == '(':
            count += 1
        elif text[i] == ')':
            count -= 1
            if count == 0:
                return i
    return -1

def check_convention(file_path):
    """
    엄격한 코딩 규칙을 체크합니다. (Kotlin 파일 전용)
    """
    if not file_path.endswith('.kt') or not os.path.exists(file_path):
        return []

    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    errors = []
    lines = content.splitlines()

    for i, line in enumerate(lines):
        clean_line = line.split('//')[0].split('/*')[0]
        if '?:' in clean_line:
            errors.append(f"L{i+1}: 엘비스 연산자(?:) 사용 금지 (명시적 if-else 사용)")
        if '!!' in clean_line:
            errors.append(f"L{i+1}: 강제 캐스팅(!!) 사용 금지")

    for match in re.finditer(r'\bif\s*\(', content):
        if_start = match.start()
        paren_start = content.find('(', if_start)
        paren_end = find_balanced_parenthesis(content, paren_start)
        
        if paren_end != -1:
            after_paren = content[paren_end + 1:]
            remaining = after_paren.lstrip()
            if not remaining.startswith('{'):
                before_if = content[:if_start].rstrip()
                if not before_if.endswith('else'):
                    line_no = content.count('\n', 0, if_start) + 1
                    errors.append(f"L{line_no}: if 문에 중괄호({{ }}) 블록 누락")

    for match in re.finditer(r'\belse\b', content):
        else_start = match.start()
        after_else = content[else_start + 4:].lstrip()
        if not (after_else.startswith('{') or after_else.startswith('if')):
            line_no = content.count('\n', 0, else_start) + 1
            errors.append(f"L{line_no}: else 문에 중괄호({{ }}) 블록 누락")

    if 'runCatching' in content:
        errors.append("runCatching 사용 금지 (명시적 try-catch 사용)")
    if 'GlobalScope' in content:
        errors.append("GlobalScope 사용 금지 (적절한 CoroutineScope 사용)")

    redundant_lambda_pattern = re.compile(r'\{\s*(\w+)\s*->\s*(\w+)\.(\w+)\(\s*(?:\w+\s*=\s*)?\1\s*\)\s*\}')
    for match in redundant_lambda_pattern.finditer(content):
        line_no = content.count('\n', 0, match.start()) + 1
        errors.append(f"L{line_no}: 단순 위임형 람다 대신 메소드 참조(::) 사용 권장 (예: viewModel::method)")

    return errors

def is_doc_or_skip_file(file_path):
    if not file_path: return True
    if file_path.startswith('docs/') or 'README' in file_path or 'GEMINI' in file_path: return True
    ext = os.path.splitext(file_path)[1].lower()
    return ext in ['.md', '.txt', '.json', '.png', '.jpg', '.webp', '.svg']

def is_config_file(file_path):
    if not file_path: return False
    ext = os.path.splitext(file_path)[1].lower()
    return ext in ['.gradle', '.kts', '.toml', '.properties', '.xml', '.pro']

def main():
    try:
        input_data = json.load(sys.stdin)
        tool_input = input_data.get('tool_input', {})
        file_path = tool_input.get('file_path')
        
        if not file_path:
            print(json.dumps({"decision": "allow"}))
            return

        # 0. Skip Check (문서 및 비빌드 파일 즉시 통과)
        if is_doc_or_skip_file(file_path):
            print(json.dumps({
                "decision": "allow",
                "systemMessage": f"⏭️ 자동 검증 스킵: 문서 또는 비코드 파일 ({file_path})"
            }))
            return

        # 1. Convention Check (Kotlin 소스 코드 전용)
        if file_path.endswith('.kt'):
            convention_errors = check_convention(file_path)
            if convention_errors:
                error_details = "\n".join(convention_errors)
                print(json.dumps({
                    "decision": "deny",
                    "reason": f"⚠️ [Convention Violation] {file_path}:\n{error_details}",
                    "systemMessage": "프로젝트 컨벤션을 준수하도록 코드를 수정하세요. (메소드 참조 권장, 엘비스 연산자 금지 등)"
                }))
                return

        module_name = get_module_name(file_path)
        
        # --- [자동 검증 차등 로직] ---
        steps = []
        if is_config_file(file_path):
            # Level 1: 설정 파일은 스타일(포맷팅)만 교정
            steps = [("./gradlew spotlessApply", "스타일 교정(Spotless)")]
        else:
            # Level 2: 소스 코드는 컴파일 검증까지 수행
            if not module_name:
                steps = [("./gradlew spotlessApply", "스타일 교정(Spotless)")]
            else:
                steps = [
                    ("./gradlew spotlessApply", "스타일 교정(Spotless)"),
                    (f"./gradlew {module_name}:compileDebugKotlin", f"{module_name} 컴파일 확인")
                ]

        if not steps:
             print(json.dumps({"decision": "allow"}))
             return

        results = []
        for cmd, desc in steps:
            result = subprocess.run(cmd, shell=True, capture_output=True, text=True)
            if result.returncode != 0:
                error_msg = result.stderr if result.stderr else result.stdout
                print(json.dumps({
                    "decision": "deny",
                    "reason": f"❌ {desc} 실패:\n{error_msg[-1000:]}",
                    "systemMessage": f"{desc} 단계에서 에러가 발생했습니다. 수정을 완료해야 작업을 종료할 수 있습니다."
                }))
                return
            results.append(desc)
        
        # 모든 단계 성공
        success_msg = " -> ".join(results)
        print(json.dumps({
            "decision": "allow",
            "systemMessage": f"✅ 지능형 검증 완료: {success_msg} 성공"
        }))

    except Exception as e:
        sys.stderr.write(f"Error in post_mod hook: {str(e)}\n")
        print(json.dumps({"decision": "allow"}))

if __name__ == "__main__":
    main()
