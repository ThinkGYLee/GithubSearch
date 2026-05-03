#!/usr/bin/env python3
import sys
import json
import os
import subprocess

def is_build_logic(file_path):
    """빌드 로직 관련 파일인지 확인"""
    if not file_path:
        return False
    
    build_logic_exts = ['.gradle.kts', '.gradle', '.toml', '.properties']
    build_logic_dirs = ['build-logic', 'gradle']
    
    file_name = os.path.basename(file_path)
    # 확장자 체크
    if any(file_name.endswith(ext) for ext in build_logic_exts):
        return True
    
    # 경로 체크
    parts = file_path.split(os.sep)
    if any(d in parts for d in build_logic_dirs):
        return True
        
    return False

def is_source_code(file_path):
    """소스 코드(src/main/java) 관련 파일인지 확인"""
    if not file_path:
        return False
    return 'src/main/java' in file_path or 'src/main/kotlin' in file_path

def get_modified_files():
    """현재 수정되거나 스테이징된 파일 목록을 가져옴"""
    try:
        result = subprocess.run(['git', 'status', '--porcelain'], capture_output=True, text=True)
        if result.returncode != 0:
            return []
        
        files = []
        for line in result.stdout.splitlines():
            if len(line) > 3:
                files.append(line[3:].strip())
        return files
    except:
        return []

def main():
    try:
        input_data = json.load(sys.stdin)
        tool_input = input_data.get('tool_input', {})
        file_path = tool_input.get('file_path')
        
        if not file_path:
            print(json.dumps({"decision": "allow"}))
            return

        # --- [Strict Scope Adherence 감시] ---
        modified_files = get_modified_files()
        current_is_build = is_build_logic(file_path)
        current_is_source = is_source_code(file_path)
        
        has_modified_build = any(is_build_logic(f) for f in modified_files)
        has_modified_source = any(is_source_code(f) for f in modified_files)
        
        # 빌드 로직 수정 중 소스 코드를 건드리려 할 때
        if current_is_source and has_modified_build:
            print(json.dumps({
                "decision": "deny",
                "reason": "❌ [Strict Scope Adherence 위반]: 현재 빌드 로직(Gradle/build-logic) 수정이 진행 중입니다. 소스 코드를 동시에 수정할 수 없습니다. 빌드 로직 수정을 먼저 마무리(커밋 또는 되돌리기)하세요.",
                "systemMessage": "Identity의 'Strict Scope Adherence' 원칙에 따라 빌드 로직과 소스 코드의 동시 수정을 차단했습니다."
            }))
            return

        # 소스 코드 수정 중 빌드 로직을 건드리려 할 때
        if current_is_build and has_modified_source:
            print(json.dumps({
                "decision": "deny",
                "reason": "❌ [Strict Scope Adherence 위반]: 현재 소스 코드(src/main/java) 수정이 진행 중입니다. 빌드 로직을 동시에 수정할 수 없습니다. 소스 코드 수정을 먼저 마무리(커밋 또는 되돌리기)하세요.",
                "systemMessage": "Identity의 'Strict Scope Adherence' 원칙에 따라 소스 코드와 빌드 로직의 동시 수정을 차단했습니다."
            }))
            return

        # --- [수정 계획 보고] ---
        instruction = tool_input.get('instruction', 'No instruction provided')
        report = f"\n--- [Pre-Modification Hook: Surgical Update Check] ---\n"
        report += f"대상 파일: {file_path}\n"
        report += f"수정 유형: {'Build Logic' if current_is_build else 'Source Code' if current_is_source else 'Other'}\n"
        report += f"수정 계획: {instruction}\n"
        report += f"----------------------------------------------------\n"
        report += "위 계획대로 수정을 진행할까요? (Enter: 진행, Ctrl+C: 취소)"

        print(json.dumps({
            "decision": "allow",
            "systemMessage": report
        }))

    except Exception as e:
        sys.stderr.write(f"Error in pre_mod hook: {str(e)}\n")
        print(json.dumps({"decision": "allow"}))

if __name__ == "__main__":
    main()
